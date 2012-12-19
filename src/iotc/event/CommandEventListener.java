package iotc.event;

import iotc.db.Log;
import iotc.db.User;

/**
 * 通信媒体がコマンドを受信した際のイベントリスナ
 * @author atsushi-o
 */
public interface CommandEventListener {
    /**
     * コマンド受信イベント
     * @param user 送信元ユーザ
     * @param command コマンド本文
     * @param reply このイベントに対応するログインスタンス
     */
    void onReceiveCommand(User user, String command, Log log);
}
