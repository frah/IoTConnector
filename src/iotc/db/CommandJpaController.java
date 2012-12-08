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
public class CommandJpaController implements Serializable {

    public CommandJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Command command) {
        if (command.getTaskList() == null) {
            command.setTaskList(new ArrayList<Task>());
        }
        if (command.getLogList() == null) {
            command.setLogList(new ArrayList<Log>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Device deviceId = command.getDeviceId();
            if (deviceId != null) {
                deviceId = em.getReference(deviceId.getClass(), deviceId.getId());
                command.setDeviceId(deviceId);
            }
            List<Task> attachedTaskList = new ArrayList<Task>();
            for (Task taskListTaskToAttach : command.getTaskList()) {
                taskListTaskToAttach = em.getReference(taskListTaskToAttach.getClass(), taskListTaskToAttach.getId());
                attachedTaskList.add(taskListTaskToAttach);
            }
            command.setTaskList(attachedTaskList);
            List<Log> attachedLogList = new ArrayList<Log>();
            for (Log logListLogToAttach : command.getLogList()) {
                logListLogToAttach = em.getReference(logListLogToAttach.getClass(), logListLogToAttach.getId());
                attachedLogList.add(logListLogToAttach);
            }
            command.setLogList(attachedLogList);
            em.persist(command);
            if (deviceId != null) {
                deviceId.getCommandList().add(command);
                deviceId = em.merge(deviceId);
            }
            for (Task taskListTask : command.getTaskList()) {
                taskListTask.getCommandList().add(command);
                taskListTask = em.merge(taskListTask);
            }
            for (Log logListLog : command.getLogList()) {
                Command oldComIdOfLogListLog = logListLog.getComId();
                logListLog.setComId(command);
                logListLog = em.merge(logListLog);
                if (oldComIdOfLogListLog != null) {
                    oldComIdOfLogListLog.getLogList().remove(logListLog);
                    oldComIdOfLogListLog = em.merge(oldComIdOfLogListLog);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Command command) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Command persistentCommand = em.find(Command.class, command.getId());
            Device deviceIdOld = persistentCommand.getDeviceId();
            Device deviceIdNew = command.getDeviceId();
            List<Task> taskListOld = persistentCommand.getTaskList();
            List<Task> taskListNew = command.getTaskList();
            List<Log> logListOld = persistentCommand.getLogList();
            List<Log> logListNew = command.getLogList();
            if (deviceIdNew != null) {
                deviceIdNew = em.getReference(deviceIdNew.getClass(), deviceIdNew.getId());
                command.setDeviceId(deviceIdNew);
            }
            List<Task> attachedTaskListNew = new ArrayList<Task>();
            for (Task taskListNewTaskToAttach : taskListNew) {
                taskListNewTaskToAttach = em.getReference(taskListNewTaskToAttach.getClass(), taskListNewTaskToAttach.getId());
                attachedTaskListNew.add(taskListNewTaskToAttach);
            }
            taskListNew = attachedTaskListNew;
            command.setTaskList(taskListNew);
            List<Log> attachedLogListNew = new ArrayList<Log>();
            for (Log logListNewLogToAttach : logListNew) {
                logListNewLogToAttach = em.getReference(logListNewLogToAttach.getClass(), logListNewLogToAttach.getId());
                attachedLogListNew.add(logListNewLogToAttach);
            }
            logListNew = attachedLogListNew;
            command.setLogList(logListNew);
            command = em.merge(command);
            if (deviceIdOld != null && !deviceIdOld.equals(deviceIdNew)) {
                deviceIdOld.getCommandList().remove(command);
                deviceIdOld = em.merge(deviceIdOld);
            }
            if (deviceIdNew != null && !deviceIdNew.equals(deviceIdOld)) {
                deviceIdNew.getCommandList().add(command);
                deviceIdNew = em.merge(deviceIdNew);
            }
            for (Task taskListOldTask : taskListOld) {
                if (!taskListNew.contains(taskListOldTask)) {
                    taskListOldTask.getCommandList().remove(command);
                    taskListOldTask = em.merge(taskListOldTask);
                }
            }
            for (Task taskListNewTask : taskListNew) {
                if (!taskListOld.contains(taskListNewTask)) {
                    taskListNewTask.getCommandList().add(command);
                    taskListNewTask = em.merge(taskListNewTask);
                }
            }
            for (Log logListOldLog : logListOld) {
                if (!logListNew.contains(logListOldLog)) {
                    logListOldLog.setComId(null);
                    logListOldLog = em.merge(logListOldLog);
                }
            }
            for (Log logListNewLog : logListNew) {
                if (!logListOld.contains(logListNewLog)) {
                    Command oldComIdOfLogListNewLog = logListNewLog.getComId();
                    logListNewLog.setComId(command);
                    logListNewLog = em.merge(logListNewLog);
                    if (oldComIdOfLogListNewLog != null && !oldComIdOfLogListNewLog.equals(command)) {
                        oldComIdOfLogListNewLog.getLogList().remove(logListNewLog);
                        oldComIdOfLogListNewLog = em.merge(oldComIdOfLogListNewLog);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = command.getId();
                if (findCommand(id) == null) {
                    throw new NonexistentEntityException("The command with id " + id + " no longer exists.");
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
            Command command;
            try {
                command = em.getReference(Command.class, id);
                command.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The command with id " + id + " no longer exists.", enfe);
            }
            Device deviceId = command.getDeviceId();
            if (deviceId != null) {
                deviceId.getCommandList().remove(command);
                deviceId = em.merge(deviceId);
            }
            List<Task> taskList = command.getTaskList();
            for (Task taskListTask : taskList) {
                taskListTask.getCommandList().remove(command);
                taskListTask = em.merge(taskListTask);
            }
            List<Log> logList = command.getLogList();
            for (Log logListLog : logList) {
                logListLog.setComId(null);
                logListLog = em.merge(logListLog);
            }
            em.remove(command);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Command> findCommandEntities() {
        return findCommandEntities(true, -1, -1);
    }

    public List<Command> findCommandEntities(int maxResults, int firstResult) {
        return findCommandEntities(false, maxResults, firstResult);
    }

    private List<Command> findCommandEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Command.class));
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

    public Command findCommand(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Command.class, id);
        } finally {
            em.close();
        }
    }

    public int getCommandCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Command> rt = cq.from(Command.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
