package iotc;

import java.util.Arrays;
import java.util.ArrayList;
import org.itolab.morihit.clinkx.*;
import iotc.event.UPnPEventListener;

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
        // TODO: implement this
    }

    /**
     * UPnPデバイスが検出されなくなった際に呼ばれる
     * <p>DBを参照し，既に登録されているデバイスであればそのレコードを，
     * なければレコードを生成して#onFailDevice()イベントで返す</p>
     * @param upprd
     */
    @Override
    public void deviceRemoved(UPnPRemoteDevice upprd) {
        // TODO: implement
        /*
         * DB参照→登録があればそのDeviceレコードを，
         * なければレコードを生成して#onFailDevice()を呼ぶ
         */
    }

    @Override
    public void deviceStateChanged(UPnPRemoteStateVariable upprsv) {}
}
