package iotc;

import iotc.common.UPnPException;
import iotc.db.Device;
import iotc.db.EntityMapUtil;
import iotc.db.HibernateUtil;
import iotc.db.Sensor;
import iotc.event.DBEventListener;
import iotc.event.DBEventListenerManager;
import iotc.event.UPnPEventListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.Session;
import org.itolab.morihit.clinkx.*;

/**
 * UPnPデバイスを管理するクラス
 * @author atsushi-o
 */
public class UPnPDevices implements UPnPDeviceChangeListener, DBEventListener {
    /* UPnP */
    private final UPnPControlPoint controlPoint;
    private HashMap<String, UPnPRemoteDevice> availableDevices;
    private HashMap<String, Boolean> deviceStates;
    private ArrayList<UPnPEventListener> listeners;

    private static final Logger LOG;
    private static final UPnPDevices instance;
    static {
        LOG = Logger.getLogger(UPnPDevices.class.getName());
        instance = new UPnPDevices();
    }

    /**
     * UPnPDevicesのインスタンスを得る
     * @return UPnPDevicesのインスタンス
     */
    public static UPnPDevices getInstance() {
        return UPnPDevices.instance;
    }
    private UPnPDevices() {
        availableDevices = new HashMap();
        deviceStates = new HashMap();
        listeners = new ArrayList();
        DBEventListenerManager.getInstance().addListener(this, "Sensor");

        /* UPnP購読スレッドを開始 */
        controlPoint = new UPnPControlPoint();
        controlPoint.setDeviceChangeListener(this);
        controlPoint.start();
    }

    /**
     * UPnP購読スレッドを停止する
     */
    public void stop() {
        controlPoint.stop();
    }

    /**
     * UPnPイベントリスナを登録する
     * @param listener
     */
    public void addListener(UPnPEventListener ...listener) {
        listeners.addAll(Arrays.asList(listener));
    }
    /**
     * UPnPイベントリスナを登録解除する
     * @param listener
     */
    public void removeListener(UPnPEventListener ...listener) {
        listeners.removeAll(Arrays.asList(listener));
    }

    /**
     * 現在検出しているUPnPデバイスの実体を返す
     * @param udn UPnPデバイスのUDN
     * @return
     */
    public UPnPRemoteDevice getAvailableUPnPDevice(String udn) {
        return availableDevices.get(udn);
    }

    /**
     * デバイスの状態を設定する
     * @param udn UPnPデバイスのUDN
     * @param state 設定する状態
     */
    public void setDeviceState(String udn, boolean state) {
        if (deviceStates.containsKey(udn)) {
            deviceStates.put(udn, state);
        }
    }

    /**
     * デバイスの状態を切り替える
     * @param udn UPnPデバイスのUDN
     */
    public void toggleDeviceState(String udn) {
        if (deviceStates.containsKey(udn)) {
            deviceStates.put(udn, !deviceStates.get(udn));
        }
    }

    /**
     * デバイスの状態を取得する
     * @param udn UPnPデバイスのUDN
     * @return 現在の状態
     */
    public boolean getDeviceState(String udn) {
        if (deviceStates.containsKey(udn)) return deviceStates.get(udn);
        return false;
    }

    /**
     * 新しいUPnPデバイスを検出した際に呼ばれる
     * <p>DBを参照し，既に登録されているデバイスであればそのレコードを#onDetectKnownDevice()イベントで返す．</p>
     * <p>登録がなければ，#onDetectNewDevice()を呼ぶ</p>
     * @param upprd
     */
    @Override
    public void deviceAdded(UPnPRemoteDevice upprd) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        Device d = (Device)s.getNamedQuery("Device.findFromUDN").setString("udn", upprd.getUDN()).uniqueResult();
        if (d != null) {
            for (UPnPEventListener l : listeners) {
                l.onDetectKnownDevice(d);
            }
        } else {
            for (UPnPEventListener l : listeners) {
                l.onDetectNewDevice(upprd);
            }
        }

        availableDevices.put(upprd.getUDN(), upprd);
        /*
        TODO: state管理の実装
            現在 → 検出時は電源OFFで固定
         */
        deviceStates.put(upprd.getUDN(), false);

        // Subscribe all variable of device
        if (d != null) {
            for (Sensor sens : (Set<Sensor>)d.getSensors()) {
                subscribe(sens);
            }
        }

        s.close();
    }

    /**
     * UPnPデバイスが検出されなくなった際に呼ばれる
     * <p>DBを参照し，既に登録されているデバイスであればそのレコードを，
     * なければレコードを生成して#onFailDevice()イベントで返す</p>
     * @param upprd
     */
    @Override
    public void deviceRemoved(UPnPRemoteDevice upprd) {
        availableDevices.remove(upprd.getUDN());

        Session s = HibernateUtil.getSessionFactory().openSession();
        Device d = (Device)s.getNamedQuery("Device.findFromUDN").setString("udn", upprd.getUDN()).uniqueResult();
        if (d != null) {
            for (UPnPEventListener l : listeners) {
                l.onFailDevice(d);
            }
        } else {
            for (UPnPEventListener l : listeners) {
                d = new Device(upprd);
                l.onFailDevice(d);
            }
        }
        s.close();
    }

    @Override
    public void deviceStateChanged(UPnPRemoteStateVariable upprsv) {
        for (UPnPEventListener l : listeners) {
            l.onUpdateValue(upprsv);
        }
    }

    @Override public void onCreate(String entityName, Object entity) {
        subscribe((Sensor)entity);
    }

    @Override public void onDelete(String entityName, Object entity) {
        unsubscribe((Sensor)entity);
    }

    @Override public void onUpdate(String entityName, Object entity) {
        subscribe((Sensor)entity);
    }

    private void subscribe(Sensor sensor) {
        try {
            UPnPRemoteStateVariable upprsv = EntityMapUtil.dbToUPnP((Sensor) sensor);
            if (!upprsv.subscribe()) {
                LOG.log(Level.INFO, "Failed to subscribe UPnPRemoteStateVariable '{0}'", upprsv.getName());
            }
        } catch (UPnPException ex) {
            LOG.log(Level.WARNING, "Failed to get UPnPRemoteStateVariable '{0}'", sensor);
        }
    }
    private void unsubscribe(Sensor sensor) {
        try {
            UPnPRemoteStateVariable upprsv = EntityMapUtil.dbToUPnP((Sensor) sensor);
            if (!upprsv.unsubscribe()) {
                LOG.log(Level.INFO, "Failed to unsubscribe UPnPRemoteStateVariable '{0}'", upprsv.getName());
            }
        } catch (UPnPException ex) {
            LOG.log(Level.WARNING, "Failed to get UPnPRemoteStateVariable '{0}'", sensor);
        }
    }
}
