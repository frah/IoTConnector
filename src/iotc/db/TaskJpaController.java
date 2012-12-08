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
public class TaskJpaController implements Serializable {

    public TaskJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Task task) {
        if (task.getCommandList() == null) {
            task.setCommandList(new ArrayList<Command>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Term term = task.getTerm();
            if (term != null) {
                term = em.getReference(term.getClass(), term.getId());
                task.setTerm(term);
            }
            List<Command> attachedCommandList = new ArrayList<Command>();
            for (Command commandListCommandToAttach : task.getCommandList()) {
                commandListCommandToAttach = em.getReference(commandListCommandToAttach.getClass(), commandListCommandToAttach.getId());
                attachedCommandList.add(commandListCommandToAttach);
            }
            task.setCommandList(attachedCommandList);
            em.persist(task);
            if (term != null) {
                term.getTaskList().add(task);
                term = em.merge(term);
            }
            for (Command commandListCommand : task.getCommandList()) {
                commandListCommand.getTaskList().add(task);
                commandListCommand = em.merge(commandListCommand);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Task task) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Task persistentTask = em.find(Task.class, task.getId());
            Term termOld = persistentTask.getTerm();
            Term termNew = task.getTerm();
            List<Command> commandListOld = persistentTask.getCommandList();
            List<Command> commandListNew = task.getCommandList();
            if (termNew != null) {
                termNew = em.getReference(termNew.getClass(), termNew.getId());
                task.setTerm(termNew);
            }
            List<Command> attachedCommandListNew = new ArrayList<Command>();
            for (Command commandListNewCommandToAttach : commandListNew) {
                commandListNewCommandToAttach = em.getReference(commandListNewCommandToAttach.getClass(), commandListNewCommandToAttach.getId());
                attachedCommandListNew.add(commandListNewCommandToAttach);
            }
            commandListNew = attachedCommandListNew;
            task.setCommandList(commandListNew);
            task = em.merge(task);
            if (termOld != null && !termOld.equals(termNew)) {
                termOld.getTaskList().remove(task);
                termOld = em.merge(termOld);
            }
            if (termNew != null && !termNew.equals(termOld)) {
                termNew.getTaskList().add(task);
                termNew = em.merge(termNew);
            }
            for (Command commandListOldCommand : commandListOld) {
                if (!commandListNew.contains(commandListOldCommand)) {
                    commandListOldCommand.getTaskList().remove(task);
                    commandListOldCommand = em.merge(commandListOldCommand);
                }
            }
            for (Command commandListNewCommand : commandListNew) {
                if (!commandListOld.contains(commandListNewCommand)) {
                    commandListNewCommand.getTaskList().add(task);
                    commandListNewCommand = em.merge(commandListNewCommand);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = task.getId();
                if (findTask(id) == null) {
                    throw new NonexistentEntityException("The task with id " + id + " no longer exists.");
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
            Task task;
            try {
                task = em.getReference(Task.class, id);
                task.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The task with id " + id + " no longer exists.", enfe);
            }
            Term term = task.getTerm();
            if (term != null) {
                term.getTaskList().remove(task);
                term = em.merge(term);
            }
            List<Command> commandList = task.getCommandList();
            for (Command commandListCommand : commandList) {
                commandListCommand.getTaskList().remove(task);
                commandListCommand = em.merge(commandListCommand);
            }
            em.remove(task);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Task> findTaskEntities() {
        return findTaskEntities(true, -1, -1);
    }

    public List<Task> findTaskEntities(int maxResults, int firstResult) {
        return findTaskEntities(false, maxResults, firstResult);
    }

    private List<Task> findTaskEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Task.class));
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

    public Task findTask(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Task.class, id);
        } finally {
            em.close();
        }
    }

    public int getTaskCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Task> rt = cq.from(Task.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
