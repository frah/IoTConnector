package iotc;

import iotc.db.Device;
import iotc.db.HibernateUtil;
import iotc.event.UPnPEventListener;
import java.util.ArrayList;
import java.util.Arrays;
import org.itolab.morihit.clinkx.UPnPControlPoint;
import org.itolab.morihit.clinkx.UPnPDeviceChangeListener;
import org.itolab.morihit.clinkx.UPnPRemoteDevice;
import org.itolab.morihit.clinkx.UPnPRemoteStateVariable;
import org.hibernate.Session;

/**
 * UPnPデバイスを管理するクラス
 * @author atsushi-o
 */
public class UPnPDevices implements UPnPDeviceChangeListener {
    /* UPnP */
    private final UPnPControlPoint controlPoint;
    private ArrayList<UPnPEventListener> listeners;

    private static final UPnPDevices instance;
    static {
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
        listeners = new ArrayList();

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
                d = new Device(upprd);
                l.onDetectNewDevice(d);
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
    public void deviceStateChanged(UPnPRemoteStateVariable upprsv) {}
}
