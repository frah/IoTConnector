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
public class StatisticValueJpaController implements Serializable {

    public StatisticValueJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(StatisticValue statisticValue) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            StatisticalMethod method = statisticValue.getMethod();
            if (method != null) {
                method = em.getReference(method.getClass(), method.getId());
                statisticValue.setMethod(method);
            }
            em.persist(statisticValue);
            if (method != null) {
                method.getStatisticValueList().add(statisticValue);
                method = em.merge(method);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(StatisticValue statisticValue) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            StatisticValue persistentStatisticValue = em.find(StatisticValue.class, statisticValue.getId());
            StatisticalMethod methodOld = persistentStatisticValue.getMethod();
            StatisticalMethod methodNew = statisticValue.getMethod();
            if (methodNew != null) {
                methodNew = em.getReference(methodNew.getClass(), methodNew.getId());
                statisticValue.setMethod(methodNew);
            }
            statisticValue = em.merge(statisticValue);
            if (methodOld != null && !methodOld.equals(methodNew)) {
                methodOld.getStatisticValueList().remove(statisticValue);
                methodOld = em.merge(methodOld);
            }
            if (methodNew != null && !methodNew.equals(methodOld)) {
                methodNew.getStatisticValueList().add(statisticValue);
                methodNew = em.merge(methodNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = statisticValue.getId();
                if (findStatisticValue(id) == null) {
                    throw new NonexistentEntityException("The statisticValue with id " + id + " no longer exists.");
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
            StatisticValue statisticValue;
            try {
                statisticValue = em.getReference(StatisticValue.class, id);
                statisticValue.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The statisticValue with id " + id + " no longer exists.", enfe);
            }
            StatisticalMethod method = statisticValue.getMethod();
            if (method != null) {
                method.getStatisticValueList().remove(statisticValue);
                method = em.merge(method);
            }
            em.remove(statisticValue);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<StatisticValue> findStatisticValueEntities() {
        return findStatisticValueEntities(true, -1, -1);
    }

    public List<StatisticValue> findStatisticValueEntities(int maxResults, int firstResult) {
        return findStatisticValueEntities(false, maxResults, firstResult);
    }

    private List<StatisticValue> findStatisticValueEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(StatisticValue.class));
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

    public StatisticValue findStatisticValue(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(StatisticValue.class, id);
        } finally {
            em.close();
        }
    }

    public int getStatisticValueCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<StatisticValue> rt = cq.from(StatisticValue.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
