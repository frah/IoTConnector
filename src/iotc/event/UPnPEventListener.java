package iotc.event;

import iotc.db.Device;
import org.itolab.morihit.clinkx.UPnPRemoteDevice;

/**
 * UPnPイベントのリスナ
 * @author atsushi-o
 */
public interface UPnPEventListener {
    /**
     * 新しいUPnPデバイスが検出された時
     * @param device
     */
    void onDetectNewDevice(UPnPRemoteDevice device);
    /**
     * 既存のUPnPデバイスが検出された時
     * @param device
     */
    void onDetectKnownDevice(Device device);
    /**
     * UPnPデバイスが検出できなくなった時
     * @param device
     */
    void onFailDevice(Device device);
}
