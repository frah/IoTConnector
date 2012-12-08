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
public class TermJpaController implements Serializable {

    public TermJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Term term) {
        if (term.getSensorList() == null) {
            term.setSensorList(new ArrayList<Sensor>());
        }
        if (term.getStatisticalMethodList() == null) {
            term.setStatisticalMethodList(new ArrayList<StatisticalMethod>());
        }
        if (term.getTaskList() == null) {
            term.setTaskList(new ArrayList<Task>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Sensor> attachedSensorList = new ArrayList<Sensor>();
            for (Sensor sensorListSensorToAttach : term.getSensorList()) {
                sensorListSensorToAttach = em.getReference(sensorListSensorToAttach.getClass(), sensorListSensorToAttach.getId());
                attachedSensorList.add(sensorListSensorToAttach);
            }
            term.setSensorList(attachedSensorList);
            List<StatisticalMethod> attachedStatisticalMethodList = new ArrayList<StatisticalMethod>();
            for (StatisticalMethod statisticalMethodListStatisticalMethodToAttach : term.getStatisticalMethodList()) {
                statisticalMethodListStatisticalMethodToAttach = em.getReference(statisticalMethodListStatisticalMethodToAttach.getClass(), statisticalMethodListStatisticalMethodToAttach.getId());
                attachedStatisticalMethodList.add(statisticalMethodListStatisticalMethodToAttach);
            }
            term.setStatisticalMethodList(attachedStatisticalMethodList);
            List<Task> attachedTaskList = new ArrayList<Task>();
            for (Task taskListTaskToAttach : term.getTaskList()) {
                taskListTaskToAttach = em.getReference(taskListTaskToAttach.getClass(), taskListTaskToAttach.getId());
                attachedTaskList.add(taskListTaskToAttach);
            }
            term.setTaskList(attachedTaskList);
            em.persist(term);
            for (Sensor sensorListSensor : term.getSensorList()) {
                sensorListSensor.getTermList().add(term);
                sensorListSensor = em.merge(sensorListSensor);
            }
            for (StatisticalMethod statisticalMethodListStatisticalMethod : term.getStatisticalMethodList()) {
                statisticalMethodListStatisticalMethod.getTermList().add(term);
                statisticalMethodListStatisticalMethod = em.merge(statisticalMethodListStatisticalMethod);
            }
            for (Task taskListTask : term.getTaskList()) {
                Term oldTermOfTaskListTask = taskListTask.getTerm();
                taskListTask.setTerm(term);
                taskListTask = em.merge(taskListTask);
                if (oldTermOfTaskListTask != null) {
                    oldTermOfTaskListTask.getTaskList().remove(taskListTask);
                    oldTermOfTaskListTask = em.merge(oldTermOfTaskListTask);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Term term) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Term persistentTerm = em.find(Term.class, term.getId());
            List<Sensor> sensorListOld = persistentTerm.getSensorList();
            List<Sensor> sensorListNew = term.getSensorList();
            List<StatisticalMethod> statisticalMethodListOld = persistentTerm.getStatisticalMethodList();
            List<StatisticalMethod> statisticalMethodListNew = term.getStatisticalMethodList();
            List<Task> taskListOld = persistentTerm.getTaskList();
            List<Task> taskListNew = term.getTaskList();
            List<String> illegalOrphanMessages = null;
            for (Task taskListOldTask : taskListOld) {
                if (!taskListNew.contains(taskListOldTask)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Task " + taskListOldTask + " since its term field is not nullable.");
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
            term.setSensorList(sensorListNew);
            List<StatisticalMethod> attachedStatisticalMethodListNew = new ArrayList<StatisticalMethod>();
            for (StatisticalMethod statisticalMethodListNewStatisticalMethodToAttach : statisticalMethodListNew) {
                statisticalMethodListNewStatisticalMethodToAttach = em.getReference(statisticalMethodListNewStatisticalMethodToAttach.getClass(), statisticalMethodListNewStatisticalMethodToAttach.getId());
                attachedStatisticalMethodListNew.add(statisticalMethodListNewStatisticalMethodToAttach);
            }
            statisticalMethodListNew = attachedStatisticalMethodListNew;
            term.setStatisticalMethodList(statisticalMethodListNew);
            List<Task> attachedTaskListNew = new ArrayList<Task>();
            for (Task taskListNewTaskToAttach : taskListNew) {
                taskListNewTaskToAttach = em.getReference(taskListNewTaskToAttach.getClass(), taskListNewTaskToAttach.getId());
                attachedTaskListNew.add(taskListNewTaskToAttach);
            }
            taskListNew = attachedTaskListNew;
            term.setTaskList(taskListNew);
            term = em.merge(term);
            for (Sensor sensorListOldSensor : sensorListOld) {
                if (!sensorListNew.contains(sensorListOldSensor)) {
                    sensorListOldSensor.getTermList().remove(term);
                    sensorListOldSensor = em.merge(sensorListOldSensor);
                }
            }
            for (Sensor sensorListNewSensor : sensorListNew) {
                if (!sensorListOld.contains(sensorListNewSensor)) {
                    sensorListNewSensor.getTermList().add(term);
                    sensorListNewSensor = em.merge(sensorListNewSensor);
                }
            }
            for (StatisticalMethod statisticalMethodListOldStatisticalMethod : statisticalMethodListOld) {
                if (!statisticalMethodListNew.contains(statisticalMethodListOldStatisticalMethod)) {
                    statisticalMethodListOldStatisticalMethod.getTermList().remove(term);
                    statisticalMethodListOldStatisticalMethod = em.merge(statisticalMethodListOldStatisticalMethod);
                }
            }
            for (StatisticalMethod statisticalMethodListNewStatisticalMethod : statisticalMethodListNew) {
                if (!statisticalMethodListOld.contains(statisticalMethodListNewStatisticalMethod)) {
                    statisticalMethodListNewStatisticalMethod.getTermList().add(term);
                    statisticalMethodListNewStatisticalMethod = em.merge(statisticalMethodListNewStatisticalMethod);
                }
            }
            for (Task taskListNewTask : taskListNew) {
                if (!taskListOld.contains(taskListNewTask)) {
                    Term oldTermOfTaskListNewTask = taskListNewTask.getTerm();
                    taskListNewTask.setTerm(term);
                    taskListNewTask = em.merge(taskListNewTask);
                    if (oldTermOfTaskListNewTask != null && !oldTermOfTaskListNewTask.equals(term)) {
                        oldTermOfTaskListNewTask.getTaskList().remove(taskListNewTask);
                        oldTermOfTaskListNewTask = em.merge(oldTermOfTaskListNewTask);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = term.getId();
                if (findTerm(id) == null) {
                    throw new NonexistentEntityException("The term with id " + id + " no longer exists.");
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
            Term term;
            try {
                term = em.getReference(Term.class, id);
                term.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The term with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Task> taskListOrphanCheck = term.getTaskList();
            for (Task taskListOrphanCheckTask : taskListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Term (" + term + ") cannot be destroyed since the Task " + taskListOrphanCheckTask + " in its taskList field has a non-nullable term field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Sensor> sensorList = term.getSensorList();
            for (Sensor sensorListSensor : sensorList) {
                sensorListSensor.getTermList().remove(term);
                sensorListSensor = em.merge(sensorListSensor);
            }
            List<StatisticalMethod> statisticalMethodList = term.getStatisticalMethodList();
            for (StatisticalMethod statisticalMethodListStatisticalMethod : statisticalMethodList) {
                statisticalMethodListStatisticalMethod.getTermList().remove(term);
                statisticalMethodListStatisticalMethod = em.merge(statisticalMethodListStatisticalMethod);
            }
            em.remove(term);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Term> findTermEntities() {
        return findTermEntities(true, -1, -1);
    }

    public List<Term> findTermEntities(int maxResults, int firstResult) {
        return findTermEntities(false, maxResults, firstResult);
    }

    private List<Term> findTermEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Term.class));
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

    public Term findTerm(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Term.class, id);
        } finally {
            em.close();
        }
    }

    public int getTermCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Term> rt = cq.from(Term.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
