package iotc.db;

/**
 * コマンドのタイプ
 * @author atsushi-o
 */
public enum CommandType {
    /** SunSPOTを介した赤外線制御コマンド */
    SunSPOT     (1, "SunSPOT InfraRed"),
    /** それ以外のUPnPコマンド */
    UPnPAction  (2, "UPnP Action");

    private final int id;
    private final String exp;
    private CommandType(int id, String exp) {
        this.id = id;
        this.exp = exp;
    }

    public int getId() {
        return this.id;
    }

    public static CommandType valueOf(int id) {
        for (CommandType d : values()) {
            if (d.id == id) return d;
        }
        return null;
    }

    @Override
    public String toString() {
        return this.exp;
    }
}
