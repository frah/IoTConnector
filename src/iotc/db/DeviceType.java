package iotc.db;

/**
 *
 * @author atsushi-o
 */
public enum DeviceType {
    SunSPOT (1, "SunSPOT"),
    Other   (2, "Other UPnP device");

    private final int type_id;
    private final String type_name;
    private DeviceType(int id, String name) {
        type_id = id;
        type_name = name;
    }

    public int getId() {
        return type_id;
    }

    public static DeviceType valueOf(int id) {
        for (DeviceType d : values()) {
            if (d.type_id == id) return d;
        }
        return null;
    }

    @Override
    public String toString() {
        return this.type_name;
    }
}
