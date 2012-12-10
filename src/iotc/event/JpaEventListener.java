package iotc.event;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

/**
 * JPAコールバッククラス
 * <p>DBの追加，削除，変更時にDBEventListenerにイベント通知</p>
 * @author atsushi-o
 */
public class JpaEventListener {
    @PostPersist
    public void onPostPersist(Object o) {
        DBEventListenerManager.getInstance().fireOnCreate(o.getClass().getSimpleName(), o);
    }

    @PostRemove
    public void onPostRemove(Object o) {
        DBEventListenerManager.getInstance().fireOnDelete(o.getClass().getSimpleName(), o);
    }

    @PostUpdate
    public void onPostUpdate(Object o) {
        DBEventListenerManager.getInstance().fireOnUpdate(o.getClass().getSimpleName(), o);
    }
}
