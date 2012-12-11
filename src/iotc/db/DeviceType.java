package iotc.db;

/**
 *
 * @author atsushi-o
 */
public enum DeviceType {
    SunSPOT (1),
    Other   (2);

    private final int type_id;
    private DeviceType(int id) {
        type_id = id;
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
}
