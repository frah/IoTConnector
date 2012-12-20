package iotc.db;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import iotc.event.JpaEventListener;

/**
 * Hibernate Utility class with a convenient method to get Session Factory
 * object.
 *
 * @author atsushi-o
 */
public class HibernateUtil {

    private static final SessionFactory sessionFactory;
    private static final ServiceRegistry serviceRegistry;
    private static final JpaEventListener listener;

    static {
        try {
            // Create the SessionFactory from standard (hibernate.cfg.xml)
            // config file.
            Configuration cfg = new Configuration();
            cfg.configure();
            serviceRegistry = new ServiceRegistryBuilder().applySettings(cfg.getProperties()).buildServiceRegistry();
            listener = new JpaEventListener();
            EventListenerRegistry els = serviceRegistry.getService(EventListenerRegistry.class);
            els.appendListeners(EventType.POST_INSERT, listener);
            els.appendListeners(EventType.POST_DELETE, listener);
            els.appendListeners(EventType.POST_UPDATE, listener);
            sessionFactory = cfg.buildSessionFactory(serviceRegistry);
        } catch (Throwable ex) {
            // Log the exception.
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
