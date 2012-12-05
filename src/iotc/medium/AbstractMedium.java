package iotc.medium;

import java.util.Arrays;
import java.util.ArrayList;
import iotc.event.CommandEventListener;

/**
 * 通信媒体の抽象クラス
 * @author atsushi-o
 */
public abstract class AbstractMedium {
    private ArrayList<CommandEventListener> listeners = new ArrayList();

    /**
     * ユーザにメッセージを送信する
     * @param logId メッセージの対応するログID
     * @param userId メッセージを送信するユーザID
     * @param message メッセージ本文
     * @return 送信成否
     */
    public abstract boolean Send(int logId, int userId, String message);

    /**
     * メッセージ受信イベントのリスナを登録
     * @param listener
     */
    public void addListener(CommandEventListener ...listener) {
        listeners.addAll(Arrays.asList(listener));
    }
    /**
     * メッセージ受信イベントのリスナの登録解除
     * @param listener
     */
    public void removeListener(CommandEventListener listener) {
        listeners.remove(listener);
    }
    /**
     * リスナにコマンド受信通知を送る
     * @see iotc.event.CommandEventListener#onReceiveCommand(int, java.lang.String, int) 
     */
    protected void fireReceiveEvent(int userId, String command, int replyId) {
        for (CommandEventListener l : listeners) {
            l.onReceiveCommand(userId, command, replyId);
        }
    }
}
