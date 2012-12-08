/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package iotc.db;

import iotc.db.exceptions.NonexistentEntityException;
import iotc.db.exceptions.PreexistingEntityException;
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
public class PowerJpaController implements Serializable {

    public PowerJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Power power) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            User authBy = power.getAuthBy();
            if (authBy != null) {
                authBy = em.getReference(authBy.getClass(), authBy.getId());
                power.setAuthBy(authBy);
            }
            User user = power.getUser();
            if (user != null) {
                user = em.getReference(user.getClass(), user.getId());
                power.setUser(user);
            }
            em.persist(power);
            if (authBy != null) {
                authBy.getPowerList().add(power);
                authBy = em.merge(authBy);
            }
            if (user != null) {
                user.getPowerList().add(power);
                user = em.merge(user);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPower(power.getUserId()) != null) {
                throw new PreexistingEntityException("Power " + power + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Power power) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Power persistentPower = em.find(Power.class, power.getUserId());
            User authByOld = persistentPower.getAuthBy();
            User authByNew = power.getAuthBy();
            User userOld = persistentPower.getUser();
            User userNew = power.getUser();
            if (authByNew != null) {
                authByNew = em.getReference(authByNew.getClass(), authByNew.getId());
                power.setAuthBy(authByNew);
            }
            if (userNew != null) {
                userNew = em.getReference(userNew.getClass(), userNew.getId());
                power.setUser(userNew);
            }
            power = em.merge(power);
            if (authByOld != null && !authByOld.equals(authByNew)) {
                authByOld.getPowerList().remove(power);
                authByOld = em.merge(authByOld);
            }
            if (authByNew != null && !authByNew.equals(authByOld)) {
                authByNew.getPowerList().add(power);
                authByNew = em.merge(authByNew);
            }
            if (userOld != null && !userOld.equals(userNew)) {
                userOld.getPowerList().remove(power);
                userOld = em.merge(userOld);
            }
            if (userNew != null && !userNew.equals(userOld)) {
                userNew.getPowerList().add(power);
                userNew = em.merge(userNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = power.getUserId();
                if (findPower(id) == null) {
                    throw new NonexistentEntityException("The power with id " + id + " no longer exists.");
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
            Power power;
            try {
                power = em.getReference(Power.class, id);
                power.getUserId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The power with id " + id + " no longer exists.", enfe);
            }
            User authBy = power.getAuthBy();
            if (authBy != null) {
                authBy.getPowerList().remove(power);
                authBy = em.merge(authBy);
            }
            User user = power.getUser();
            if (user != null) {
                user.getPowerList().remove(power);
                user = em.merge(user);
            }
            em.remove(power);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Power> findPowerEntities() {
        return findPowerEntities(true, -1, -1);
    }

    public List<Power> findPowerEntities(int maxResults, int firstResult) {
        return findPowerEntities(false, maxResults, firstResult);
    }

    private List<Power> findPowerEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Power.class));
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

    public Power findPower(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Power.class, id);
        } finally {
            em.close();
        }
    }

    public int getPowerCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Power> rt = cq.from(Power.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
