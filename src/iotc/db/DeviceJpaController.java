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
public class DeviceJpaController implements Serializable {

    public DeviceJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Device device) {
        if (device.getSensorList() == null) {
            device.setSensorList(new ArrayList<Sensor>());
        }
        if (device.getCommandList() == null) {
            device.setCommandList(new ArrayList<Command>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Room roomId = device.getRoomId();
            if (roomId != null) {
                roomId = em.getReference(roomId.getClass(), roomId.getId());
                device.setRoomId(roomId);
            }
            List<Sensor> attachedSensorList = new ArrayList<Sensor>();
            for (Sensor sensorListSensorToAttach : device.getSensorList()) {
                sensorListSensorToAttach = em.getReference(sensorListSensorToAttach.getClass(), sensorListSensorToAttach.getId());
                attachedSensorList.add(sensorListSensorToAttach);
            }
            device.setSensorList(attachedSensorList);
            List<Command> attachedCommandList = new ArrayList<Command>();
            for (Command commandListCommandToAttach : device.getCommandList()) {
                commandListCommandToAttach = em.getReference(commandListCommandToAttach.getClass(), commandListCommandToAttach.getId());
                attachedCommandList.add(commandListCommandToAttach);
            }
            device.setCommandList(attachedCommandList);
            em.persist(device);
            if (roomId != null) {
                roomId.getDeviceList().add(device);
                roomId = em.merge(roomId);
            }
            for (Sensor sensorListSensor : device.getSensorList()) {
                Device oldDeviceIdOfSensorListSensor = sensorListSensor.getDeviceId();
                sensorListSensor.setDeviceId(device);
                sensorListSensor = em.merge(sensorListSensor);
                if (oldDeviceIdOfSensorListSensor != null) {
                    oldDeviceIdOfSensorListSensor.getSensorList().remove(sensorListSensor);
                    oldDeviceIdOfSensorListSensor = em.merge(oldDeviceIdOfSensorListSensor);
                }
            }
            for (Command commandListCommand : device.getCommandList()) {
                Device oldDeviceIdOfCommandListCommand = commandListCommand.getDeviceId();
                commandListCommand.setDeviceId(device);
                commandListCommand = em.merge(commandListCommand);
                if (oldDeviceIdOfCommandListCommand != null) {
                    oldDeviceIdOfCommandListCommand.getCommandList().remove(commandListCommand);
                    oldDeviceIdOfCommandListCommand = em.merge(oldDeviceIdOfCommandListCommand);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Device device) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Device persistentDevice = em.find(Device.class, device.getId());
            Room roomIdOld = persistentDevice.getRoomId();
            Room roomIdNew = device.getRoomId();
            List<Sensor> sensorListOld = persistentDevice.getSensorList();
            List<Sensor> sensorListNew = device.getSensorList();
            List<Command> commandListOld = persistentDevice.getCommandList();
            List<Command> commandListNew = device.getCommandList();
            List<String> illegalOrphanMessages = null;
            for (Sensor sensorListOldSensor : sensorListOld) {
                if (!sensorListNew.contains(sensorListOldSensor)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Sensor " + sensorListOldSensor + " since its deviceId field is not nullable.");
                }
            }
            for (Command commandListOldCommand : commandListOld) {
                if (!commandListNew.contains(commandListOldCommand)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Command " + commandListOldCommand + " since its deviceId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (roomIdNew != null) {
                roomIdNew = em.getReference(roomIdNew.getClass(), roomIdNew.getId());
                device.setRoomId(roomIdNew);
            }
            List<Sensor> attachedSensorListNew = new ArrayList<Sensor>();
            for (Sensor sensorListNewSensorToAttach : sensorListNew) {
                sensorListNewSensorToAttach = em.getReference(sensorListNewSensorToAttach.getClass(), sensorListNewSensorToAttach.getId());
                attachedSensorListNew.add(sensorListNewSensorToAttach);
            }
            sensorListNew = attachedSensorListNew;
            device.setSensorList(sensorListNew);
            List<Command> attachedCommandListNew = new ArrayList<Command>();
            for (Command commandListNewCommandToAttach : commandListNew) {
                commandListNewCommandToAttach = em.getReference(commandListNewCommandToAttach.getClass(), commandListNewCommandToAttach.getId());
                attachedCommandListNew.add(commandListNewCommandToAttach);
            }
            commandListNew = attachedCommandListNew;
            device.setCommandList(commandListNew);
            device = em.merge(device);
            if (roomIdOld != null && !roomIdOld.equals(roomIdNew)) {
                roomIdOld.getDeviceList().remove(device);
                roomIdOld = em.merge(roomIdOld);
            }
            if (roomIdNew != null && !roomIdNew.equals(roomIdOld)) {
                roomIdNew.getDeviceList().add(device);
                roomIdNew = em.merge(roomIdNew);
            }
            for (Sensor sensorListNewSensor : sensorListNew) {
                if (!sensorListOld.contains(sensorListNewSensor)) {
                    Device oldDeviceIdOfSensorListNewSensor = sensorListNewSensor.getDeviceId();
                    sensorListNewSensor.setDeviceId(device);
                    sensorListNewSensor = em.merge(sensorListNewSensor);
                    if (oldDeviceIdOfSensorListNewSensor != null && !oldDeviceIdOfSensorListNewSensor.equals(device)) {
                        oldDeviceIdOfSensorListNewSensor.getSensorList().remove(sensorListNewSensor);
                        oldDeviceIdOfSensorListNewSensor = em.merge(oldDeviceIdOfSensorListNewSensor);
                    }
                }
            }
            for (Command commandListNewCommand : commandListNew) {
                if (!commandListOld.contains(commandListNewCommand)) {
                    Device oldDeviceIdOfCommandListNewCommand = commandListNewCommand.getDeviceId();
                    commandListNewCommand.setDeviceId(device);
                    commandListNewCommand = em.merge(commandListNewCommand);
                    if (oldDeviceIdOfCommandListNewCommand != null && !oldDeviceIdOfCommandListNewCommand.equals(device)) {
                        oldDeviceIdOfCommandListNewCommand.getCommandList().remove(commandListNewCommand);
                        oldDeviceIdOfCommandListNewCommand = em.merge(oldDeviceIdOfCommandListNewCommand);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = device.getId();
                if (findDevice(id) == null) {
                    throw new NonexistentEntityException("The device with id " + id + " no longer exists.");
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
            Device device;
            try {
                device = em.getReference(Device.class, id);
                device.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The device with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Sensor> sensorListOrphanCheck = device.getSensorList();
            for (Sensor sensorListOrphanCheckSensor : sensorListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Device (" + device + ") cannot be destroyed since the Sensor " + sensorListOrphanCheckSensor + " in its sensorList field has a non-nullable deviceId field.");
            }
            List<Command> commandListOrphanCheck = device.getCommandList();
            for (Command commandListOrphanCheckCommand : commandListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Device (" + device + ") cannot be destroyed since the Command " + commandListOrphanCheckCommand + " in its commandList field has a non-nullable deviceId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Room roomId = device.getRoomId();
            if (roomId != null) {
                roomId.getDeviceList().remove(device);
                roomId = em.merge(roomId);
            }
            em.remove(device);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Device> findDeviceEntities() {
        return findDeviceEntities(true, -1, -1);
    }

    public List<Device> findDeviceEntities(int maxResults, int firstResult) {
        return findDeviceEntities(false, maxResults, firstResult);
    }

    private List<Device> findDeviceEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Device.class));
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

    public Device findDevice(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Device.class, id);
        } finally {
            em.close();
        }
    }

    public int getDeviceCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Device> rt = cq.from(Device.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
