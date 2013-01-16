package iotc.db;

/**
 * 権限を表す列挙型
 * @author atsushi-o
 */
public enum PowerEnum {
    ANONYMOUS       (0, "Anonymous"),
    GUEST           (1, "Guest"),
    FAMILY          (3, "Family"),
    OWNER           (4, "Device owner"),
    ADMINISTRATOR   (9, "Administrator");

    private final int id;
    private final String str;
    private PowerEnum(final int id, final String str) {
        this.id = id;
        this.str = str;
    }
    public int getId() {
        return id;
    }

    public static PowerEnum valueOf(Integer id) {
        if (id == null) return null;
        for (PowerEnum p : values()) {
            if (p.getId() == id) return p;
        }
        return null;
    }
    @Override public String toString() {
        return str + " (" + id + ")";
    }
}
