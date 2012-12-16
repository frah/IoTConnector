package iotc.test;

import java.util.UUID;
import org.itolab.morihit.clinkx.*;

/**
 * ダミーSunSPOTデバイス
 * @author atsushi-o
 */
public class DummySunSPOTDevice implements UPnPActionListener {
    private final UPnPDevice device;

    private UPnPIntegerStateVariable com;
    private UPnPStringStateVariable irCom;
    private UPnPStringStateVariable address;

    public DummySunSPOTDevice() {
        this("SunSPOT-"+UUID.randomUUID().toString());
    }
    public DummySunSPOTDevice(String friendlyName) {
        device = new FixedUdnUPnPDevice(friendlyName);

        com = new UPnPIntegerStateVariable("Command");
        irCom = new UPnPStringStateVariable("IRCommand");
        address = new UPnPStringStateVariable("Address");

        UPnPService service = new UPnPService();
        service.addStateVariable(com);
        com.publish();
        UPnPAction a = new UPnPSetIntegerAction(com.getName());
        a.setActionListener(this);
        service.addAction(a);
        a = new UPnPGetIntegerAction(com.getName());
        a.setActionListener(this);
        service.addAction(a);
        addVariable(service, irCom, address);
        device.addService(service);

        device.start();
    }

    public void stop() {
        device.stop();
    }

    private void addVariable(UPnPService service, UPnPStringStateVariable ...variables) {
        for (UPnPStateVariable val : variables) {
            service.addStateVariable(val);
            val.publish();
            UPnPAction action = new UPnPSetStringAction(val.getName());
            action.setActionListener(this);
            service.addAction(action);
            action = new UPnPGetStringAction(val.getName());
            action.setActionListener(this);
            service.addAction(action);
        }
    }

    public synchronized void setCommand(int i) {
        switch (i) {
            case 1: {
                break;
            }
            case 2: {
                irCom.setValue("kUcJGwkbCQkJCQkJCQkJCQkbCQkJCQkaCgkJGgkaChoKGgoaChoKGgkbCggKCAkaCggKCAoICggKCAoaChoKBwoaCg==");
                break;
            }
        }
    }
    public synchronized int getCommand() {
        return com.getValue();
    }

    public synchronized void setIRCommand(String s) {
        irCom.setValue("OK");
    }
    public synchronized String getIRCommand() {
        return irCom.getValue();
    }

    public synchronized void setAddress(String s) {}
    public synchronized String getAddress() {
        return address.getValue();
    }
}
