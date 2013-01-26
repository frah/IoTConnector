package iotc.test;

/**
 * 実験用イベントリスナ
 * User: atsushi-o
 * Date: 13/01/27
 * Time: 6:06
 */
public interface ExpEventListener {
    /**
     * コマンドを受信した時に発生
     * @param mediumId 受信したメディアID
     */
    void onReceiveCommand(String mediumId);
    /**
     * コマンドが実行完了した時に発生
     * @param mediumId コマンドのメディアID
     */
    void onCommandComplete(String mediumId);
}
