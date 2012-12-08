/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package iotc.db;

import iotc.db.exceptions.NonexistentEntityException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;

/**
 *
 * @author atsushi-o
 */
public class SensorValueJpaController implements Serializable {

    public SensorValueJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(SensorValue sensorValue) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Sensor sensor = sensorValue.getSensor();
            if (sensor != null) {
                sensor = em.getReference(sensor.getClass(), sensor.getId());
                sensorValue.setSensor(sensor);
            }
            em.persist(sensorValue);
            if (sensor != null) {
                sensor.getSensorValueList().add(sensorValue);
                sensor = em.merge(sensor);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(SensorValue sensorValue) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SensorValue persistentSensorValue = em.find(SensorValue.class, sensorValue.getId());
            Sensor sensorOld = persistentSensorValue.getSensor();
            Sensor sensorNew = sensorValue.getSensor();
            if (sensorNew != null) {
                sensorNew = em.getReference(sensorNew.getClass(), sensorNew.getId());
                sensorValue.setSensor(sensorNew);
            }
            sensorValue = em.merge(sensorValue);
            if (sensorOld != null && !sensorOld.equals(sensorNew)) {
                sensorOld.getSensorValueList().remove(sensorValue);
                sensorOld = em.merge(sensorOld);
            }
            if (sensorNew != null && !sensorNew.equals(sensorOld)) {
                sensorNew.getSensorValueList().add(sensorValue);
                sensorNew = em.merge(sensorNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = sensorValue.getId();
                if (findSensorValue(id) == null) {
                    throw new NonexistentEntityException("The sensorValue with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SensorValue sensorValue;
            try {
                sensorValue = em.getReference(SensorValue.class, id);
                sensorValue.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The sensorValue with id " + id + " no longer exists.", enfe);
            }
            Sensor sensor = sensorValue.getSensor();
            if (sensor != null) {
                sensor.getSensorValueList().remove(sensorValue);
                sensor = em.merge(sensor);
            }
            em.remove(sensorValue);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<SensorValue> findSensorValueEntities() {
        return findSensorValueEntities(true, -1, -1);
    }

    public List<SensorValue> findSensorValueEntities(int maxResults, int firstResult) {
        return findSensorValueEntities(false, maxResults, firstResult);
    }

    private List<SensorValue> findSensorValueEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(SensorValue.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public SensorValue findSensorValue(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(SensorValue.class, id);
        } finally {
            em.close();
        }
    }

    public int getSensorValueCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<SensorValue> rt = cq.from(SensorValue.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
