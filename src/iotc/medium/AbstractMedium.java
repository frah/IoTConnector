package iotc.medium;

import iotc.db.Log;
import iotc.db.User;
import iotc.event.CommandEventListener;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * 通信媒体の抽象クラス
 * @author atsushi-o
 */
public abstract class AbstractMedium implements Medium {
    private ArrayList<CommandEventListener> listeners = new ArrayList();

    /**
     * メッセージ受信イベントのリスナを登録
     * @param listener
     */
    @Override
    public void addListener(CommandEventListener listener) {
        listeners.add(listener);
    }

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
    @Override
    public void removeListener(CommandEventListener listener) {
        listeners.remove(listener);
    }
    /**
     * リスナにコマンド受信通知を送る
     * @see iotc.event.CommandEventListener#onReceiveCommand(iotc.db.User, java.lang.String, iotc.db.Log)
     */
    protected void fireReceiveEvent(User user, String command, Log reply) {
        for (CommandEventListener l : listeners) {
            l.onReceiveCommand(this, user, command, reply);
        }
    }
}
