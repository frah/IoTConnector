package iotc.medium;

import iotc.db.HibernateUtil;
import iotc.db.Log;
import iotc.db.LogState;
import iotc.db.User;
import iotc.event.CommandEventListener;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 通信媒体の抽象クラス
 * @author atsushi-o
 */
public abstract class AbstractMedium implements Medium {
    private ArrayList<CommandEventListener> listeners = new ArrayList();

    /**
     * メッセージ送信の内部処理
     * @param user メッセージ送信ユーザ
     * @param message メッセージ本文
     * @param replyId リプライ先ID，なければnull
     * @return 送信したメッセージのID
     */
    protected abstract String _send(User user, String message, String replyId);
    @Override
    public final boolean send(Log log, User user, String message) {
        String mediumID = _send(user, message, log!=null?log.getMediumId():null);

        Log newLog = new Log();
        newLog.setState(mediumID!=null?LogState.COMPLETE.getId():LogState.ERROR.getId());
        newLog.setMediumId(mediumID);
        newLog.setLog(log);
        newLog.setUser(user);
        newLog.setComVariable(message);

        Session s = HibernateUtil.getSessionFactory().openSession();
        s.beginTransaction();
        try {
            s.save(newLog);
            s.getTransaction().commit();
        } catch (HibernateException ex) {
            s.getTransaction().rollback();
        }
        s.close();

        return mediumID != null;
    }

    /**
     * メッセージ受信イベントのリスナを登録
     * @param listener
     */
    @Override
    public final void addListener(CommandEventListener listener) {
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
    public final void removeListener(CommandEventListener listener) {
        listeners.remove(listener);
    }
    /**
     * リスナにコマンド受信通知を送る
     * @see iotc.event.CommandEventListener#onReceiveCommand(Medium sender, User user, String command, Log log)
     */
    protected void fireReceiveEvent(User user, String command, Log reply) {
        for (CommandEventListener l : listeners) {
            l.onReceiveCommand(this, user, command, reply);
        }
    }
}
