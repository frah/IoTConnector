package iotc.medium;

import iotc.db.Log;
import iotc.db.User;
import iotc.event.CommandEventListener;

/**
 * 通信媒体のインターフェース
 * @author atsushi-o
 */
public interface Medium {
    /**
     * ユーザにメッセージを送信する
     * @param logId メッセージの対応するログID
     * @param userId メッセージを送信するユーザID
     * @param message メッセージ本文
     * @return 送信成否
     */
    boolean Send(Log log, User user, String message);
    /**
     * メッセージ受信イベントのリスナを登録
     * @param listener
     */
    void addListener(CommandEventListener listener);
    /**
     * メッセージ受信イベントのリスナの登録解除
     * @param listener
     */
    void removeListener(CommandEventListener listener);
}
