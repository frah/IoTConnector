/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package iotc.db;

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
public class LogJpaController implements Serializable {

    public LogJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Log log) {
        if (log.getLogList() == null) {
            log.setLogList(new ArrayList<Log>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Log relationalLog = log.getRelationalLog();
            if (relationalLog != null) {
                relationalLog = em.getReference(relationalLog.getClass(), relationalLog.getId());
                log.setRelationalLog(relationalLog);
            }
            Command comId = log.getComId();
            if (comId != null) {
                comId = em.getReference(comId.getClass(), comId.getId());
                log.setComId(comId);
            }
            User userId = log.getUserId();
            if (userId != null) {
                userId = em.getReference(userId.getClass(), userId.getId());
                log.setUserId(userId);
            }
            List<Log> attachedLogList = new ArrayList<Log>();
            for (Log logListLogToAttach : log.getLogList()) {
                logListLogToAttach = em.getReference(logListLogToAttach.getClass(), logListLogToAttach.getId());
                attachedLogList.add(logListLogToAttach);
            }
            log.setLogList(attachedLogList);
            em.persist(log);
            if (relationalLog != null) {
                relationalLog.getLogList().add(log);
                relationalLog = em.merge(relationalLog);
            }
            if (comId != null) {
                comId.getLogList().add(log);
                comId = em.merge(comId);
            }
            if (userId != null) {
                userId.getLogList().add(log);
                userId = em.merge(userId);
            }
            for (Log logListLog : log.getLogList()) {
                Log oldRelationalLogOfLogListLog = logListLog.getRelationalLog();
                logListLog.setRelationalLog(log);
                logListLog = em.merge(logListLog);
                if (oldRelationalLogOfLogListLog != null) {
                    oldRelationalLogOfLogListLog.getLogList().remove(logListLog);
                    oldRelationalLogOfLogListLog = em.merge(oldRelationalLogOfLogListLog);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Log log) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Log persistentLog = em.find(Log.class, log.getId());
            Log relationalLogOld = persistentLog.getRelationalLog();
            Log relationalLogNew = log.getRelationalLog();
            Command comIdOld = persistentLog.getComId();
            Command comIdNew = log.getComId();
            User userIdOld = persistentLog.getUserId();
            User userIdNew = log.getUserId();
            List<Log> logListOld = persistentLog.getLogList();
            List<Log> logListNew = log.getLogList();
            if (relationalLogNew != null) {
                relationalLogNew = em.getReference(relationalLogNew.getClass(), relationalLogNew.getId());
                log.setRelationalLog(relationalLogNew);
            }
            if (comIdNew != null) {
                comIdNew = em.getReference(comIdNew.getClass(), comIdNew.getId());
                log.setComId(comIdNew);
            }
            if (userIdNew != null) {
                userIdNew = em.getReference(userIdNew.getClass(), userIdNew.getId());
                log.setUserId(userIdNew);
            }
            List<Log> attachedLogListNew = new ArrayList<Log>();
            for (Log logListNewLogToAttach : logListNew) {
                logListNewLogToAttach = em.getReference(logListNewLogToAttach.getClass(), logListNewLogToAttach.getId());
                attachedLogListNew.add(logListNewLogToAttach);
            }
            logListNew = attachedLogListNew;
            log.setLogList(logListNew);
            log = em.merge(log);
            if (relationalLogOld != null && !relationalLogOld.equals(relationalLogNew)) {
                relationalLogOld.getLogList().remove(log);
                relationalLogOld = em.merge(relationalLogOld);
            }
            if (relationalLogNew != null && !relationalLogNew.equals(relationalLogOld)) {
                relationalLogNew.getLogList().add(log);
                relationalLogNew = em.merge(relationalLogNew);
            }
            if (comIdOld != null && !comIdOld.equals(comIdNew)) {
                comIdOld.getLogList().remove(log);
                comIdOld = em.merge(comIdOld);
            }
            if (comIdNew != null && !comIdNew.equals(comIdOld)) {
                comIdNew.getLogList().add(log);
                comIdNew = em.merge(comIdNew);
            }
            if (userIdOld != null && !userIdOld.equals(userIdNew)) {
                userIdOld.getLogList().remove(log);
                userIdOld = em.merge(userIdOld);
            }
            if (userIdNew != null && !userIdNew.equals(userIdOld)) {
                userIdNew.getLogList().add(log);
                userIdNew = em.merge(userIdNew);
            }
            for (Log logListOldLog : logListOld) {
                if (!logListNew.contains(logListOldLog)) {
                    logListOldLog.setRelationalLog(null);
                    logListOldLog = em.merge(logListOldLog);
                }
            }
            for (Log logListNewLog : logListNew) {
                if (!logListOld.contains(logListNewLog)) {
                    Log oldRelationalLogOfLogListNewLog = logListNewLog.getRelationalLog();
                    logListNewLog.setRelationalLog(log);
                    logListNewLog = em.merge(logListNewLog);
                    if (oldRelationalLogOfLogListNewLog != null && !oldRelationalLogOfLogListNewLog.equals(log)) {
                        oldRelationalLogOfLogListNewLog.getLogList().remove(logListNewLog);
                        oldRelationalLogOfLogListNewLog = em.merge(oldRelationalLogOfLogListNewLog);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = log.getId();
                if (findLog(id) == null) {
                    throw new NonexistentEntityException("The log with id " + id + " no longer exists.");
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
            Log log;
            try {
                log = em.getReference(Log.class, id);
                log.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The log with id " + id + " no longer exists.", enfe);
            }
            Log relationalLog = log.getRelationalLog();
            if (relationalLog != null) {
                relationalLog.getLogList().remove(log);
                relationalLog = em.merge(relationalLog);
            }
            Command comId = log.getComId();
            if (comId != null) {
                comId.getLogList().remove(log);
                comId = em.merge(comId);
            }
            User userId = log.getUserId();
            if (userId != null) {
                userId.getLogList().remove(log);
                userId = em.merge(userId);
            }
            List<Log> logList = log.getLogList();
            for (Log logListLog : logList) {
                logListLog.setRelationalLog(null);
                logListLog = em.merge(logListLog);
            }
            em.remove(log);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Log> findLogEntities() {
        return findLogEntities(true, -1, -1);
    }

    public List<Log> findLogEntities(int maxResults, int firstResult) {
        return findLogEntities(false, maxResults, firstResult);
    }

    private List<Log> findLogEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Log.class));
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

    public Log findLog(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Log.class, id);
        } finally {
            em.close();
        }
    }

    public int getLogCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Log> rt = cq.from(Log.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
