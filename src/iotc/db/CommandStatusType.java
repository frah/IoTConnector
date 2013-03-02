package iotc.db;

/**
 * コマンドがステートに影響をおよぼすかどうか
 */
public enum CommandStatusType {
    /** ステート変化なし */
    NONE    (0, "None"),
    /** state -&#62; true */
    ON      (1, "On"),
    /** state -&#62; false */
    OFF     (2, "Off"),
    /** state -&#62; on &#60;-&#62; */
    TOGGLE  (3, "Toggle");

    private final int id;
    private final String exp;
    private CommandStatusType(int id, String exp) {
        this.id = id;
        this.exp = exp;
    }

    public int getId() {
        return this.id;
    }

    public static CommandStatusType valueOf(int id) {
        for (CommandStatusType d : values()) {
            if (d.id == id) return d;
        }
        return null;
    }

    @Override
    public String toString() {
        return this.exp;
    }
}
