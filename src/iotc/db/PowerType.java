package iotc.db;

/**
 * 権限の種類を表す列挙型
 * @author atsushi-o
 */
public enum PowerType {
    BASIC (0),
    DATE_LIMITED (1),
    COUNT_LIMITED (2);

    private final int id;
    private PowerType(final int id) {
        this.id = id;
    }
    public int getId() {
        return this.id;
    }
}
