package iotc.event;

/**
 * データベースの変更のイベントリスナ
 * @author atsushi-o
 */
public interface DBEventListener {
    /**
     * 新しいレコードが作成された時
     * @param entityName テーブル名
     * @param entity 作成されたレコード
     */
    void onCreate(String entityName, Object entity);
    /**
     * レコードが削除された時
     * @param entityName テーブル名
     * @param entity 削除されたレコード
     */
    void onDelete(String entityName, Object entity);
    /**
     * レコードが更新された時
     * @param entityName テーブル名
     * @param entity 更新されたレコード
     */
    void onUpdate(String entityName, Object entity);
}
