package iotc.event;

import org.hibernate.event.*;


/**
 * Hibernateコールバッククラス
 * <p>DBの追加，削除，変更時にDBEventListenerにイベント通知</p>
 * @author atsushi-o
 */
public class HibernateEventListener implements PostInsertEventListener, PostDeleteEventListener, PostUpdateEventListener {
    @Override
    public void onPostInsert(PostInsertEvent pie) {
        Object o = pie.getEntity();
        DBEventListenerManager.getInstance().fireOnCreate(o.getClass().getSimpleName(), o);
    }

    @Override
    public void onPostDelete(PostDeleteEvent pde) {
        Object o = pde.getEntity();
        DBEventListenerManager.getInstance().fireOnDelete(o.getClass().getSimpleName(), o);
    }

    @Override
    public void onPostUpdate(PostUpdateEvent pue) {
        Object o = pue.getEntity();
        DBEventListenerManager.getInstance().fireOnUpdate(o.getClass().getSimpleName(), o);
    }
}
