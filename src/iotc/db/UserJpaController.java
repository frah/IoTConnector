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
public class UserJpaController implements Serializable {

    public UserJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(User user) {
        if (user.getLogList() == null) {
            user.setLogList(new ArrayList<Log>());
        }
        if (user.getPowerList() == null) {
            user.setPowerList(new ArrayList<Power>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Power power = user.getPower();
            if (power != null) {
                power = em.getReference(power.getClass(), power.getUserId());
                user.setPower(power);
            }
            List<Log> attachedLogList = new ArrayList<Log>();
            for (Log logListLogToAttach : user.getLogList()) {
                logListLogToAttach = em.getReference(logListLogToAttach.getClass(), logListLogToAttach.getId());
                attachedLogList.add(logListLogToAttach);
            }
            user.setLogList(attachedLogList);
            List<Power> attachedPowerList = new ArrayList<Power>();
            for (Power powerListPowerToAttach : user.getPowerList()) {
                powerListPowerToAttach = em.getReference(powerListPowerToAttach.getClass(), powerListPowerToAttach.getUserId());
                attachedPowerList.add(powerListPowerToAttach);
            }
            user.setPowerList(attachedPowerList);
            em.persist(user);
            if (power != null) {
                User oldUserOfPower = power.getUser();
                if (oldUserOfPower != null) {
                    oldUserOfPower.setPower(null);
                    oldUserOfPower = em.merge(oldUserOfPower);
                }
                power.setUser(user);
                power = em.merge(power);
            }
            for (Log logListLog : user.getLogList()) {
                User oldUserIdOfLogListLog = logListLog.getUserId();
                logListLog.setUserId(user);
                logListLog = em.merge(logListLog);
                if (oldUserIdOfLogListLog != null) {
                    oldUserIdOfLogListLog.getLogList().remove(logListLog);
                    oldUserIdOfLogListLog = em.merge(oldUserIdOfLogListLog);
                }
            }
            for (Power powerListPower : user.getPowerList()) {
                User oldAuthByOfPowerListPower = powerListPower.getAuthBy();
                powerListPower.setAuthBy(user);
                powerListPower = em.merge(powerListPower);
                if (oldAuthByOfPowerListPower != null) {
                    oldAuthByOfPowerListPower.getPowerList().remove(powerListPower);
                    oldAuthByOfPowerListPower = em.merge(oldAuthByOfPowerListPower);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(User user) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            User persistentUser = em.find(User.class, user.getId());
            Power powerOld = persistentUser.getPower();
            Power powerNew = user.getPower();
            List<Log> logListOld = persistentUser.getLogList();
            List<Log> logListNew = user.getLogList();
            List<Power> powerListOld = persistentUser.getPowerList();
            List<Power> powerListNew = user.getPowerList();
            List<String> illegalOrphanMessages = null;
            if (powerOld != null && !powerOld.equals(powerNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain Power " + powerOld + " since its user field is not nullable.");
            }
            for (Log logListOldLog : logListOld) {
                if (!logListNew.contains(logListOldLog)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Log " + logListOldLog + " since its userId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (powerNew != null) {
                powerNew = em.getReference(powerNew.getClass(), powerNew.getUserId());
                user.setPower(powerNew);
            }
            List<Log> attachedLogListNew = new ArrayList<Log>();
            for (Log logListNewLogToAttach : logListNew) {
                logListNewLogToAttach = em.getReference(logListNewLogToAttach.getClass(), logListNewLogToAttach.getId());
                attachedLogListNew.add(logListNewLogToAttach);
            }
            logListNew = attachedLogListNew;
            user.setLogList(logListNew);
            List<Power> attachedPowerListNew = new ArrayList<Power>();
            for (Power powerListNewPowerToAttach : powerListNew) {
                powerListNewPowerToAttach = em.getReference(powerListNewPowerToAttach.getClass(), powerListNewPowerToAttach.getUserId());
                attachedPowerListNew.add(powerListNewPowerToAttach);
            }
            powerListNew = attachedPowerListNew;
            user.setPowerList(powerListNew);
            user = em.merge(user);
            if (powerNew != null && !powerNew.equals(powerOld)) {
                User oldUserOfPower = powerNew.getUser();
                if (oldUserOfPower != null) {
                    oldUserOfPower.setPower(null);
                    oldUserOfPower = em.merge(oldUserOfPower);
                }
                powerNew.setUser(user);
                powerNew = em.merge(powerNew);
            }
            for (Log logListNewLog : logListNew) {
                if (!logListOld.contains(logListNewLog)) {
                    User oldUserIdOfLogListNewLog = logListNewLog.getUserId();
                    logListNewLog.setUserId(user);
                    logListNewLog = em.merge(logListNewLog);
                    if (oldUserIdOfLogListNewLog != null && !oldUserIdOfLogListNewLog.equals(user)) {
                        oldUserIdOfLogListNewLog.getLogList().remove(logListNewLog);
                        oldUserIdOfLogListNewLog = em.merge(oldUserIdOfLogListNewLog);
                    }
                }
            }
            for (Power powerListOldPower : powerListOld) {
                if (!powerListNew.contains(powerListOldPower)) {
                    powerListOldPower.setAuthBy(null);
                    powerListOldPower = em.merge(powerListOldPower);
                }
            }
            for (Power powerListNewPower : powerListNew) {
                if (!powerListOld.contains(powerListNewPower)) {
                    User oldAuthByOfPowerListNewPower = powerListNewPower.getAuthBy();
                    powerListNewPower.setAuthBy(user);
                    powerListNewPower = em.merge(powerListNewPower);
                    if (oldAuthByOfPowerListNewPower != null && !oldAuthByOfPowerListNewPower.equals(user)) {
                        oldAuthByOfPowerListNewPower.getPowerList().remove(powerListNewPower);
                        oldAuthByOfPowerListNewPower = em.merge(oldAuthByOfPowerListNewPower);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = user.getId();
                if (findUser(id) == null) {
                    throw new NonexistentEntityException("The user with id " + id + " no longer exists.");
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
            User user;
            try {
                user = em.getReference(User.class, id);
                user.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The user with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Power powerOrphanCheck = user.getPower();
            if (powerOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This User (" + user + ") cannot be destroyed since the Power " + powerOrphanCheck + " in its power field has a non-nullable user field.");
            }
            List<Log> logListOrphanCheck = user.getLogList();
            for (Log logListOrphanCheckLog : logListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This User (" + user + ") cannot be destroyed since the Log " + logListOrphanCheckLog + " in its logList field has a non-nullable userId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Power> powerList = user.getPowerList();
            for (Power powerListPower : powerList) {
                powerListPower.setAuthBy(null);
                powerListPower = em.merge(powerListPower);
            }
            em.remove(user);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<User> findUserEntities() {
        return findUserEntities(true, -1, -1);
    }

    public List<User> findUserEntities(int maxResults, int firstResult) {
        return findUserEntities(false, maxResults, firstResult);
    }

    private List<User> findUserEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(User.class));
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

    public User findUser(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(User.class, id);
        } finally {
            em.close();
        }
    }

    public int getUserCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<User> rt = cq.from(User.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
