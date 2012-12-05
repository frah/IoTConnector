package iotc.event;

/**
 * 通信媒体がコマンドを受信した際のイベントリスナ
 * @author atsushi-o
 */
public interface CommandEventListener {
    /**
     * コマンド受信イベント
     * @param userId 送信元ユーザID
     * @param command コマンド本文
     * @param replyId 返答コマンドであった場合，対象のログIDが入る．そうでない場合は-1を入れること．
     */
    void onReceiveCommand(int userId, String command, int replyId);
}
