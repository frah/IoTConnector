package iotc.db;

/**
 *
 * @author atsushi-o
 */
public enum CommandType {
    SunSPOT     (1, "SunSPOT InfraRed"),
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
    @Override
    public String toString() {
        return this.exp;
    }
}
