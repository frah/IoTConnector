package iotc.db;

/**
 * 権限の種類を表す列挙型
 * @author atsushi-o
 */
public enum PowerType {
    /** 機能制限のないユーザ */
    BASIC (0x000),
    /** 制限時間のあるユーザ */
    DATE_LIMITED (0x001),
    /** 回数制限のあるユーザ */
    COUNT_LIMITED (0x010),
    /** 機能制限のあるユーザ */
    COMMAND_LIMITED (0x100);

    private final int id;
    private PowerType(final int id) {
        this.id = id;
    }
    public int getId() {
        return this.id;
    }
    public static PowerType valueOf(Integer id) {
        if (id == null) return null;
        for (PowerType t : values()) {
            if (t.id == id) return t;
        }
        return null;
    }
}
