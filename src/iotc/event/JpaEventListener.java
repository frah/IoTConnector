package iotc.event;

import org.hibernate.event.spi.*;


/**
 * JPAコールバッククラス
 * <p>DBの追加，削除，変更時にDBEventListenerにイベント通知</p>
 * @author atsushi-o
 */
public class JpaEventListener implements PostInsertEventListener, PostDeleteEventListener, PostUpdateEventListener {
    @Override
    public void onPostInsert(PostInsertEvent event) {
        Object o = event.getEntity();
        DBEventListenerManager.getInstance().fireOnCreate(o.getClass().getSimpleName(), o);
    }

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        Object o = event.getEntity();
        DBEventListenerManager.getInstance().fireOnDelete(o.getClass().getSimpleName(), o);
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        Object o = event.getEntity();
        DBEventListenerManager.getInstance().fireOnUpdate(o.getClass().getSimpleName(), o);
    }
}
