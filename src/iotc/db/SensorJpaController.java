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
public class SensorJpaController implements Serializable {

    public SensorJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Sensor sensor) {
        if (sensor.getTermList() == null) {
            sensor.setTermList(new ArrayList<Term>());
        }
        if (sensor.getStatisticalMethodList() == null) {
            sensor.setStatisticalMethodList(new ArrayList<StatisticalMethod>());
        }
        if (sensor.getSensorValueList() == null) {
            sensor.setSensorValueList(new ArrayList<SensorValue>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Device deviceId = sensor.getDeviceId();
            if (deviceId != null) {
                deviceId = em.getReference(deviceId.getClass(), deviceId.getId());
                sensor.setDeviceId(deviceId);
            }
            SensorType type = sensor.getType();
            if (type != null) {
                type = em.getReference(type.getClass(), type.getId());
                sensor.setType(type);
            }
            List<Term> attachedTermList = new ArrayList<Term>();
            for (Term termListTermToAttach : sensor.getTermList()) {
                termListTermToAttach = em.getReference(termListTermToAttach.getClass(), termListTermToAttach.getId());
                attachedTermList.add(termListTermToAttach);
            }
            sensor.setTermList(attachedTermList);
            List<StatisticalMethod> attachedStatisticalMethodList = new ArrayList<StatisticalMethod>();
            for (StatisticalMethod statisticalMethodListStatisticalMethodToAttach : sensor.getStatisticalMethodList()) {
                statisticalMethodListStatisticalMethodToAttach = em.getReference(statisticalMethodListStatisticalMethodToAttach.getClass(), statisticalMethodListStatisticalMethodToAttach.getId());
                attachedStatisticalMethodList.add(statisticalMethodListStatisticalMethodToAttach);
            }
            sensor.setStatisticalMethodList(attachedStatisticalMethodList);
            List<SensorValue> attachedSensorValueList = new ArrayList<SensorValue>();
            for (SensorValue sensorValueListSensorValueToAttach : sensor.getSensorValueList()) {
                sensorValueListSensorValueToAttach = em.getReference(sensorValueListSensorValueToAttach.getClass(), sensorValueListSensorValueToAttach.getId());
                attachedSensorValueList.add(sensorValueListSensorValueToAttach);
            }
            sensor.setSensorValueList(attachedSensorValueList);
            em.persist(sensor);
            if (deviceId != null) {
                deviceId.getSensorList().add(sensor);
                deviceId = em.merge(deviceId);
            }
            if (type != null) {
                type.getSensorList().add(sensor);
                type = em.merge(type);
            }
            for (Term termListTerm : sensor.getTermList()) {
                termListTerm.getSensorList().add(sensor);
                termListTerm = em.merge(termListTerm);
            }
            for (StatisticalMethod statisticalMethodListStatisticalMethod : sensor.getStatisticalMethodList()) {
                statisticalMethodListStatisticalMethod.getSensorList().add(sensor);
                statisticalMethodListStatisticalMethod = em.merge(statisticalMethodListStatisticalMethod);
            }
            for (SensorValue sensorValueListSensorValue : sensor.getSensorValueList()) {
                Sensor oldSensorOfSensorValueListSensorValue = sensorValueListSensorValue.getSensor();
                sensorValueListSensorValue.setSensor(sensor);
                sensorValueListSensorValue = em.merge(sensorValueListSensorValue);
                if (oldSensorOfSensorValueListSensorValue != null) {
                    oldSensorOfSensorValueListSensorValue.getSensorValueList().remove(sensorValueListSensorValue);
                    oldSensorOfSensorValueListSensorValue = em.merge(oldSensorOfSensorValueListSensorValue);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Sensor sensor) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Sensor persistentSensor = em.find(Sensor.class, sensor.getId());
            Device deviceIdOld = persistentSensor.getDeviceId();
            Device deviceIdNew = sensor.getDeviceId();
            SensorType typeOld = persistentSensor.getType();
            SensorType typeNew = sensor.getType();
            List<Term> termListOld = persistentSensor.getTermList();
            List<Term> termListNew = sensor.getTermList();
            List<StatisticalMethod> statisticalMethodListOld = persistentSensor.getStatisticalMethodList();
            List<StatisticalMethod> statisticalMethodListNew = sensor.getStatisticalMethodList();
            List<SensorValue> sensorValueListOld = persistentSensor.getSensorValueList();
            List<SensorValue> sensorValueListNew = sensor.getSensorValueList();
            List<String> illegalOrphanMessages = null;
            for (SensorValue sensorValueListOldSensorValue : sensorValueListOld) {
                if (!sensorValueListNew.contains(sensorValueListOldSensorValue)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain SensorValue " + sensorValueListOldSensorValue + " since its sensor field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (deviceIdNew != null) {
                deviceIdNew = em.getReference(deviceIdNew.getClass(), deviceIdNew.getId());
                sensor.setDeviceId(deviceIdNew);
            }
            if (typeNew != null) {
                typeNew = em.getReference(typeNew.getClass(), typeNew.getId());
                sensor.setType(typeNew);
            }
            List<Term> attachedTermListNew = new ArrayList<Term>();
            for (Term termListNewTermToAttach : termListNew) {
                termListNewTermToAttach = em.getReference(termListNewTermToAttach.getClass(), termListNewTermToAttach.getId());
                attachedTermListNew.add(termListNewTermToAttach);
            }
            termListNew = attachedTermListNew;
            sensor.setTermList(termListNew);
            List<StatisticalMethod> attachedStatisticalMethodListNew = new ArrayList<StatisticalMethod>();
            for (StatisticalMethod statisticalMethodListNewStatisticalMethodToAttach : statisticalMethodListNew) {
                statisticalMethodListNewStatisticalMethodToAttach = em.getReference(statisticalMethodListNewStatisticalMethodToAttach.getClass(), statisticalMethodListNewStatisticalMethodToAttach.getId());
                attachedStatisticalMethodListNew.add(statisticalMethodListNewStatisticalMethodToAttach);
            }
            statisticalMethodListNew = attachedStatisticalMethodListNew;
            sensor.setStatisticalMethodList(statisticalMethodListNew);
            List<SensorValue> attachedSensorValueListNew = new ArrayList<SensorValue>();
            for (SensorValue sensorValueListNewSensorValueToAttach : sensorValueListNew) {
                sensorValueListNewSensorValueToAttach = em.getReference(sensorValueListNewSensorValueToAttach.getClass(), sensorValueListNewSensorValueToAttach.getId());
                attachedSensorValueListNew.add(sensorValueListNewSensorValueToAttach);
            }
            sensorValueListNew = attachedSensorValueListNew;
            sensor.setSensorValueList(sensorValueListNew);
            sensor = em.merge(sensor);
            if (deviceIdOld != null && !deviceIdOld.equals(deviceIdNew)) {
                deviceIdOld.getSensorList().remove(sensor);
                deviceIdOld = em.merge(deviceIdOld);
            }
            if (deviceIdNew != null && !deviceIdNew.equals(deviceIdOld)) {
                deviceIdNew.getSensorList().add(sensor);
                deviceIdNew = em.merge(deviceIdNew);
            }
            if (typeOld != null && !typeOld.equals(typeNew)) {
                typeOld.getSensorList().remove(sensor);
                typeOld = em.merge(typeOld);
            }
            if (typeNew != null && !typeNew.equals(typeOld)) {
                typeNew.getSensorList().add(sensor);
                typeNew = em.merge(typeNew);
            }
            for (Term termListOldTerm : termListOld) {
                if (!termListNew.contains(termListOldTerm)) {
                    termListOldTerm.getSensorList().remove(sensor);
                    termListOldTerm = em.merge(termListOldTerm);
                }
            }
            for (Term termListNewTerm : termListNew) {
                if (!termListOld.contains(termListNewTerm)) {
                    termListNewTerm.getSensorList().add(sensor);
                    termListNewTerm = em.merge(termListNewTerm);
                }
            }
            for (StatisticalMethod statisticalMethodListOldStatisticalMethod : statisticalMethodListOld) {
                if (!statisticalMethodListNew.contains(statisticalMethodListOldStatisticalMethod)) {
                    statisticalMethodListOldStatisticalMethod.getSensorList().remove(sensor);
                    statisticalMethodListOldStatisticalMethod = em.merge(statisticalMethodListOldStatisticalMethod);
                }
            }
            for (StatisticalMethod statisticalMethodListNewStatisticalMethod : statisticalMethodListNew) {
                if (!statisticalMethodListOld.contains(statisticalMethodListNewStatisticalMethod)) {
                    statisticalMethodListNewStatisticalMethod.getSensorList().add(sensor);
                    statisticalMethodListNewStatisticalMethod = em.merge(statisticalMethodListNewStatisticalMethod);
                }
            }
            for (SensorValue sensorValueListNewSensorValue : sensorValueListNew) {
                if (!sensorValueListOld.contains(sensorValueListNewSensorValue)) {
                    Sensor oldSensorOfSensorValueListNewSensorValue = sensorValueListNewSensorValue.getSensor();
                    sensorValueListNewSensorValue.setSensor(sensor);
                    sensorValueListNewSensorValue = em.merge(sensorValueListNewSensorValue);
                    if (oldSensorOfSensorValueListNewSensorValue != null && !oldSensorOfSensorValueListNewSensorValue.equals(sensor)) {
                        oldSensorOfSensorValueListNewSensorValue.getSensorValueList().remove(sensorValueListNewSensorValue);
                        oldSensorOfSensorValueListNewSensorValue = em.merge(oldSensorOfSensorValueListNewSensorValue);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = sensor.getId();
                if (findSensor(id) == null) {
                    throw new NonexistentEntityException("The sensor with id " + id + " no longer exists.");
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
            Sensor sensor;
            try {
                sensor = em.getReference(Sensor.class, id);
                sensor.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The sensor with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<SensorValue> sensorValueListOrphanCheck = sensor.getSensorValueList();
            for (SensorValue sensorValueListOrphanCheckSensorValue : sensorValueListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Sensor (" + sensor + ") cannot be destroyed since the SensorValue " + sensorValueListOrphanCheckSensorValue + " in its sensorValueList field has a non-nullable sensor field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Device deviceId = sensor.getDeviceId();
            if (deviceId != null) {
                deviceId.getSensorList().remove(sensor);
                deviceId = em.merge(deviceId);
            }
            SensorType type = sensor.getType();
            if (type != null) {
                type.getSensorList().remove(sensor);
                type = em.merge(type);
            }
            List<Term> termList = sensor.getTermList();
            for (Term termListTerm : termList) {
                termListTerm.getSensorList().remove(sensor);
                termListTerm = em.merge(termListTerm);
            }
            List<StatisticalMethod> statisticalMethodList = sensor.getStatisticalMethodList();
            for (StatisticalMethod statisticalMethodListStatisticalMethod : statisticalMethodList) {
                statisticalMethodListStatisticalMethod.getSensorList().remove(sensor);
                statisticalMethodListStatisticalMethod = em.merge(statisticalMethodListStatisticalMethod);
            }
            em.remove(sensor);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Sensor> findSensorEntities() {
        return findSensorEntities(true, -1, -1);
    }

    public List<Sensor> findSensorEntities(int maxResults, int firstResult) {
        return findSensorEntities(false, maxResults, firstResult);
    }

    private List<Sensor> findSensorEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Sensor.class));
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

    public Sensor findSensor(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Sensor.class, id);
        } finally {
            em.close();
        }
    }

    public int getSensorCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Sensor> rt = cq.from(Sensor.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
