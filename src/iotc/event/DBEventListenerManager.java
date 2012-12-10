package iotc.event;

import java.util.ArrayList;

/**
 * データベースのイベントリスナを管理，イベントを発生
 * @author atsushi-o
 */
public class DBEventListenerManager {
    private ArrayList<DBEventListener> listeners;
    private static final DBEventListenerManager instance;
    static {
        instance = new DBEventListenerManager();
    }

    private DBEventListenerManager() {
        this.listeners = new ArrayList();
    }
    /**
     * インスタンスを得る
     * @return インスタンス
     */
    public static DBEventListenerManager getInstance() {
        return instance;
    }

    /**
     * データベースイベントリスナを登録する
     * @param listener リスナ
     */
    public void addListener(DBEventListener listener) {
        this.listeners.add(listener);
    }
    /**
     * データベースイベントリスナを削除する
     * @param listener リスナ
     */
    public void removeListener(DBEventListener listener) {
        this.listeners.remove(listener);
    }

    /**
     * onCreateイベントを発生させる
     * @see iotc.event.DBEventListener#onCreate(java.lang.String, java.lang.Object)
     */
    public void fireOnCreate(String sender, Object entity) {
        for (DBEventListener l : this.listeners) {
            l.onCreate(sender, entity);
        }
    }
    /**
     * onDeleteイベントを発生させる
     * @see iotc.event.DBEventListener#onDelete(java.lang.String, java.lang.Object)
     */
    public void fireOnDelete(String sender, Object entity) {
        for (DBEventListener l : this.listeners) {
            l.onDelete(sender, entity);
        }
    }
    /**
     * onUpdateイベントを発生させる
     * @see iotc.event.DBEventListener#onUpdate(java.lang.String, java.lang.Object) 
     */
    public void fireOnUpdate(String sender, Object entity) {
        for (DBEventListener l : this.listeners) {
            l.onUpdate(sender, entity);
        }
    }
}
