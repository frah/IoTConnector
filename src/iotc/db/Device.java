package iotc.db;

/**
 *
 * @author atsushi-o
 */
public class Device {
    private int ID;
    private int roomID;
    private String UPnPName;
    private String name;
    private String explanation;

    public Device(String upnpName) {
        this.UPnPName = upnpName;
    }
}
