package iotc.event;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Pattern;

/**
 * データベースのイベントリスナを管理，イベントを発生
 * @author atsushi-o
 */
public class DBEventListenerManager {
    private HashMap<DBEventListener,Pattern> listeners;
    private static final DBEventListenerManager instance;
    static {
        instance = new DBEventListenerManager();
    }

    private DBEventListenerManager() {
        this.listeners = new HashMap();
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
        this.listeners.put(listener, Pattern.compile(".*"));
    }
    /**
     * 特定のテーブルの場合のみ通知を行うイベントリスナを登録する
     * @param listener リスナ
     * @param regex テーブル名の正規表現
     */
    public void addListener(DBEventListener listener, String regex) {
        this.listeners.put(listener, Pattern.compile(regex));
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
        for (Entry<DBEventListener,Pattern> e : this.listeners.entrySet()) {
            if (e.getValue().matcher(sender).matches()) {
                e.getKey().onCreate(sender, entity);
            }
        }
    }
    /**
     * onDeleteイベントを発生させる
     * @see iotc.event.DBEventListener#onDelete(java.lang.String, java.lang.Object)
     */
    public void fireOnDelete(String sender, Object entity) {
        for (Entry<DBEventListener,Pattern> e : this.listeners.entrySet()) {
            if (e.getValue().matcher(sender).matches()) {
                e.getKey().onDelete(sender, entity);
            }
        }
    }
    /**
     * onUpdateイベントを発生させる
     * @see iotc.event.DBEventListener#onUpdate(java.lang.String, java.lang.Object)
     */
    public void fireOnUpdate(String sender, Object entity) {
        for (Entry<DBEventListener,Pattern> e : this.listeners.entrySet()) {
            if (e.getValue().matcher(sender).matches()) {
                e.getKey().onUpdate(sender, entity);
            }
        }
    }
}
