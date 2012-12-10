package iotc.db;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.UserTransaction;

/**
 * JpaControllerのファクトリクラス
 * @author atsushi-o
 */
public class JCF {
    private static final Logger LOG;
    static {
        LOG = Logger.getLogger(JCF.class.getName());
    }

    private JCF() {};

    /**
     * JpaControllerのインスタンスを生成する
     * @param <C> 生成するJpaControllerクラス型
     * @param controller 生成するJpaControllerクラス
     * @return JpaControllerインスタンス
     * @throws IllegalArgumentException インスタンスの生成に失敗した時
     */
    public static <C> C createController(Class<C> controller) throws IllegalArgumentException {
        Constructor<C> con;
        Class[] types = {
            UserTransaction.class,
            EntityManagerFactory.class
        };
        Object[] args = {
            null,
            getEMF()
        };
        C obj;

        try {
            con = controller.getConstructor(types);
            obj = con.newInstance(args);
        } catch (NoSuchMethodException ex) {
            LOG.log(Level.SEVERE, "Get constructor instance failed", ex);
            throw new IllegalArgumentException(ex);
        } catch (IllegalArgumentException|InstantiationException|IllegalAccessException|InvocationTargetException ex) {
            LOG.log(Level.SEVERE, "Create JpaController instance failed", ex);
            throw new IllegalArgumentException(ex);
        }

        return obj;
    }

    /**
     * IoTConnectorのEMFを生成する
     * @return EMF
     */
    public static EntityManagerFactory getEMF() {
        return Persistence.createEntityManagerFactory("IoTConnectorPU");
    }
}
