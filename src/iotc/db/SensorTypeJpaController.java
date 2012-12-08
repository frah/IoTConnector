/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package iotc.db;

import iotc.db.exceptions.IllegalOrphanException;
import iotc.db.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author atsushi-o
 */
public class SensorTypeJpaController implements Serializable {

    public SensorTypeJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(SensorType sensorType) {
        if (sensorType.getSensorList() == null) {
            sensorType.setSensorList(new ArrayList<Sensor>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Sensor> attachedSensorList = new ArrayList<Sensor>();
            for (Sensor sensorListSensorToAttach : sensorType.getSensorList()) {
                sensorListSensorToAttach = em.getReference(sensorListSensorToAttach.getClass(), sensorListSensorToAttach.getId());
                attachedSensorList.add(sensorListSensorToAttach);
            }
            sensorType.setSensorList(attachedSensorList);
            em.persist(sensorType);
            for (Sensor sensorListSensor : sensorType.getSensorList()) {
                SensorType oldTypeOfSensorListSensor = sensorListSensor.getType();
                sensorListSensor.setType(sensorType);
                sensorListSensor = em.merge(sensorListSensor);
                if (oldTypeOfSensorListSensor != null) {
                    oldTypeOfSensorListSensor.getSensorList().remove(sensorListSensor);
                    oldTypeOfSensorListSensor = em.merge(oldTypeOfSensorListSensor);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(SensorType sensorType) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SensorType persistentSensorType = em.find(SensorType.class, sensorType.getId());
            List<Sensor> sensorListOld = persistentSensorType.getSensorList();
            List<Sensor> sensorListNew = sensorType.getSensorList();
            List<String> illegalOrphanMessages = null;
            for (Sensor sensorListOldSensor : sensorListOld) {
                if (!sensorListNew.contains(sensorListOldSensor)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Sensor " + sensorListOldSensor + " since its type field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Sensor> attachedSensorListNew = new ArrayList<Sensor>();
            for (Sensor sensorListNewSensorToAttach : sensorListNew) {
                sensorListNewSensorToAttach = em.getReference(sensorListNewSensorToAttach.getClass(), sensorListNewSensorToAttach.getId());
                attachedSensorListNew.add(sensorListNewSensorToAttach);
            }
            sensorListNew = attachedSensorListNew;
            sensorType.setSensorList(sensorListNew);
            sensorType = em.merge(sensorType);
            for (Sensor sensorListNewSensor : sensorListNew) {
                if (!sensorListOld.contains(sensorListNewSensor)) {
                    SensorType oldTypeOfSensorListNewSensor = sensorListNewSensor.getType();
                    sensorListNewSensor.setType(sensorType);
                    sensorListNewSensor = em.merge(sensorListNewSensor);
                    if (oldTypeOfSensorListNewSensor != null && !oldTypeOfSensorListNewSensor.equals(sensorType)) {
                        oldTypeOfSensorListNewSensor.getSensorList().remove(sensorListNewSensor);
                        oldTypeOfSensorListNewSensor = em.merge(oldTypeOfSensorListNewSensor);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = sensorType.getId();
                if (findSensorType(id) == null) {
                    throw new NonexistentEntityException("The sensorType with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SensorType sensorType;
            try {
                sensorType = em.getReference(SensorType.class, id);
                sensorType.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The sensorType with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Sensor> sensorListOrphanCheck = sensorType.getSensorList();
            for (Sensor sensorListOrphanCheckSensor : sensorListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This SensorType (" + sensorType + ") cannot be destroyed since the Sensor " + sensorListOrphanCheckSensor + " in its sensorList field has a non-nullable type field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(sensorType);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<SensorType> findSensorTypeEntities() {
        return findSensorTypeEntities(true, -1, -1);
    }

    public List<SensorType> findSensorTypeEntities(int maxResults, int firstResult) {
        return findSensorTypeEntities(false, maxResults, firstResult);
    }

    private List<SensorType> findSensorTypeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(SensorType.class));
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

    public SensorType findSensorType(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(SensorType.class, id);
        } finally {
            em.close();
        }
    }

    public int getSensorTypeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<SensorType> rt = cq.from(SensorType.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
