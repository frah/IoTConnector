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
public class StatisticalMethodJpaController implements Serializable {

    public StatisticalMethodJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(StatisticalMethod statisticalMethod) {
        if (statisticalMethod.getSensorList() == null) {
            statisticalMethod.setSensorList(new ArrayList<Sensor>());
        }
        if (statisticalMethod.getTermList() == null) {
            statisticalMethod.setTermList(new ArrayList<Term>());
        }
        if (statisticalMethod.getStatisticValueList() == null) {
            statisticalMethod.setStatisticValueList(new ArrayList<StatisticValue>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Sensor> attachedSensorList = new ArrayList<Sensor>();
            for (Sensor sensorListSensorToAttach : statisticalMethod.getSensorList()) {
                sensorListSensorToAttach = em.getReference(sensorListSensorToAttach.getClass(), sensorListSensorToAttach.getId());
                attachedSensorList.add(sensorListSensorToAttach);
            }
            statisticalMethod.setSensorList(attachedSensorList);
            List<Term> attachedTermList = new ArrayList<Term>();
            for (Term termListTermToAttach : statisticalMethod.getTermList()) {
                termListTermToAttach = em.getReference(termListTermToAttach.getClass(), termListTermToAttach.getId());
                attachedTermList.add(termListTermToAttach);
            }
            statisticalMethod.setTermList(attachedTermList);
            List<StatisticValue> attachedStatisticValueList = new ArrayList<StatisticValue>();
            for (StatisticValue statisticValueListStatisticValueToAttach : statisticalMethod.getStatisticValueList()) {
                statisticValueListStatisticValueToAttach = em.getReference(statisticValueListStatisticValueToAttach.getClass(), statisticValueListStatisticValueToAttach.getId());
                attachedStatisticValueList.add(statisticValueListStatisticValueToAttach);
            }
            statisticalMethod.setStatisticValueList(attachedStatisticValueList);
            em.persist(statisticalMethod);
            for (Sensor sensorListSensor : statisticalMethod.getSensorList()) {
                sensorListSensor.getStatisticalMethodList().add(statisticalMethod);
                sensorListSensor = em.merge(sensorListSensor);
            }
            for (Term termListTerm : statisticalMethod.getTermList()) {
                termListTerm.getStatisticalMethodList().add(statisticalMethod);
                termListTerm = em.merge(termListTerm);
            }
            for (StatisticValue statisticValueListStatisticValue : statisticalMethod.getStatisticValueList()) {
                StatisticalMethod oldMethodOfStatisticValueListStatisticValue = statisticValueListStatisticValue.getMethod();
                statisticValueListStatisticValue.setMethod(statisticalMethod);
                statisticValueListStatisticValue = em.merge(statisticValueListStatisticValue);
                if (oldMethodOfStatisticValueListStatisticValue != null) {
                    oldMethodOfStatisticValueListStatisticValue.getStatisticValueList().remove(statisticValueListStatisticValue);
                    oldMethodOfStatisticValueListStatisticValue = em.merge(oldMethodOfStatisticValueListStatisticValue);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(StatisticalMethod statisticalMethod) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            StatisticalMethod persistentStatisticalMethod = em.find(StatisticalMethod.class, statisticalMethod.getId());
            List<Sensor> sensorListOld = persistentStatisticalMethod.getSensorList();
            List<Sensor> sensorListNew = statisticalMethod.getSensorList();
            List<Term> termListOld = persistentStatisticalMethod.getTermList();
            List<Term> termListNew = statisticalMethod.getTermList();
            List<StatisticValue> statisticValueListOld = persistentStatisticalMethod.getStatisticValueList();
            List<StatisticValue> statisticValueListNew = statisticalMethod.getStatisticValueList();
            List<String> illegalOrphanMessages = null;
            for (StatisticValue statisticValueListOldStatisticValue : statisticValueListOld) {
                if (!statisticValueListNew.contains(statisticValueListOldStatisticValue)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain StatisticValue " + statisticValueListOldStatisticValue + " since its method field is not nullable.");
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
            statisticalMethod.setSensorList(sensorListNew);
            List<Term> attachedTermListNew = new ArrayList<Term>();
            for (Term termListNewTermToAttach : termListNew) {
                termListNewTermToAttach = em.getReference(termListNewTermToAttach.getClass(), termListNewTermToAttach.getId());
                attachedTermListNew.add(termListNewTermToAttach);
            }
            termListNew = attachedTermListNew;
            statisticalMethod.setTermList(termListNew);
            List<StatisticValue> attachedStatisticValueListNew = new ArrayList<StatisticValue>();
            for (StatisticValue statisticValueListNewStatisticValueToAttach : statisticValueListNew) {
                statisticValueListNewStatisticValueToAttach = em.getReference(statisticValueListNewStatisticValueToAttach.getClass(), statisticValueListNewStatisticValueToAttach.getId());
                attachedStatisticValueListNew.add(statisticValueListNewStatisticValueToAttach);
            }
            statisticValueListNew = attachedStatisticValueListNew;
            statisticalMethod.setStatisticValueList(statisticValueListNew);
            statisticalMethod = em.merge(statisticalMethod);
            for (Sensor sensorListOldSensor : sensorListOld) {
                if (!sensorListNew.contains(sensorListOldSensor)) {
                    sensorListOldSensor.getStatisticalMethodList().remove(statisticalMethod);
                    sensorListOldSensor = em.merge(sensorListOldSensor);
                }
            }
            for (Sensor sensorListNewSensor : sensorListNew) {
                if (!sensorListOld.contains(sensorListNewSensor)) {
                    sensorListNewSensor.getStatisticalMethodList().add(statisticalMethod);
                    sensorListNewSensor = em.merge(sensorListNewSensor);
                }
            }
            for (Term termListOldTerm : termListOld) {
                if (!termListNew.contains(termListOldTerm)) {
                    termListOldTerm.getStatisticalMethodList().remove(statisticalMethod);
                    termListOldTerm = em.merge(termListOldTerm);
                }
            }
            for (Term termListNewTerm : termListNew) {
                if (!termListOld.contains(termListNewTerm)) {
                    termListNewTerm.getStatisticalMethodList().add(statisticalMethod);
                    termListNewTerm = em.merge(termListNewTerm);
                }
            }
            for (StatisticValue statisticValueListNewStatisticValue : statisticValueListNew) {
                if (!statisticValueListOld.contains(statisticValueListNewStatisticValue)) {
                    StatisticalMethod oldMethodOfStatisticValueListNewStatisticValue = statisticValueListNewStatisticValue.getMethod();
                    statisticValueListNewStatisticValue.setMethod(statisticalMethod);
                    statisticValueListNewStatisticValue = em.merge(statisticValueListNewStatisticValue);
                    if (oldMethodOfStatisticValueListNewStatisticValue != null && !oldMethodOfStatisticValueListNewStatisticValue.equals(statisticalMethod)) {
                        oldMethodOfStatisticValueListNewStatisticValue.getStatisticValueList().remove(statisticValueListNewStatisticValue);
                        oldMethodOfStatisticValueListNewStatisticValue = em.merge(oldMethodOfStatisticValueListNewStatisticValue);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = statisticalMethod.getId();
                if (findStatisticalMethod(id) == null) {
                    throw new NonexistentEntityException("The statisticalMethod with id " + id + " no longer exists.");
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
            StatisticalMethod statisticalMethod;
            try {
                statisticalMethod = em.getReference(StatisticalMethod.class, id);
                statisticalMethod.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The statisticalMethod with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<StatisticValue> statisticValueListOrphanCheck = statisticalMethod.getStatisticValueList();
            for (StatisticValue statisticValueListOrphanCheckStatisticValue : statisticValueListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This StatisticalMethod (" + statisticalMethod + ") cannot be destroyed since the StatisticValue " + statisticValueListOrphanCheckStatisticValue + " in its statisticValueList field has a non-nullable method field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Sensor> sensorList = statisticalMethod.getSensorList();
            for (Sensor sensorListSensor : sensorList) {
                sensorListSensor.getStatisticalMethodList().remove(statisticalMethod);
                sensorListSensor = em.merge(sensorListSensor);
            }
            List<Term> termList = statisticalMethod.getTermList();
            for (Term termListTerm : termList) {
                termListTerm.getStatisticalMethodList().remove(statisticalMethod);
                termListTerm = em.merge(termListTerm);
            }
            em.remove(statisticalMethod);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<StatisticalMethod> findStatisticalMethodEntities() {
        return findStatisticalMethodEntities(true, -1, -1);
    }

    public List<StatisticalMethod> findStatisticalMethodEntities(int maxResults, int firstResult) {
        return findStatisticalMethodEntities(false, maxResults, firstResult);
    }

    private List<StatisticalMethod> findStatisticalMethodEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(StatisticalMethod.class));
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

    public StatisticalMethod findStatisticalMethod(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(StatisticalMethod.class, id);
        } finally {
            em.close();
        }
    }

    public int getStatisticalMethodCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<StatisticalMethod> rt = cq.from(StatisticalMethod.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
