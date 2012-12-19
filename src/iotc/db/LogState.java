package iotc.db;

/**
 * コマンドの状況を表す列挙型
 * @author atsushi-o
 */
public enum LogState {
    /** コマンドを受信した（未パース） */
    RECEIVED    (0),
    /** パース完了 */
    PARSED      (1),
    /** モデレータの許可待ち */
    PENDING     (2),
    /** コマンド実行完了 */
    COMPLETE    (3);

    private final int id;
    private LogState(final int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
