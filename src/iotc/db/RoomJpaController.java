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
public class RoomJpaController implements Serializable {

    public RoomJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Room room) {
        if (room.getDeviceList() == null) {
            room.setDeviceList(new ArrayList<Device>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Device> attachedDeviceList = new ArrayList<Device>();
            for (Device deviceListDeviceToAttach : room.getDeviceList()) {
                deviceListDeviceToAttach = em.getReference(deviceListDeviceToAttach.getClass(), deviceListDeviceToAttach.getId());
                attachedDeviceList.add(deviceListDeviceToAttach);
            }
            room.setDeviceList(attachedDeviceList);
            em.persist(room);
            for (Device deviceListDevice : room.getDeviceList()) {
                Room oldRoomIdOfDeviceListDevice = deviceListDevice.getRoomId();
                deviceListDevice.setRoomId(room);
                deviceListDevice = em.merge(deviceListDevice);
                if (oldRoomIdOfDeviceListDevice != null) {
                    oldRoomIdOfDeviceListDevice.getDeviceList().remove(deviceListDevice);
                    oldRoomIdOfDeviceListDevice = em.merge(oldRoomIdOfDeviceListDevice);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Room room) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Room persistentRoom = em.find(Room.class, room.getId());
            List<Device> deviceListOld = persistentRoom.getDeviceList();
            List<Device> deviceListNew = room.getDeviceList();
            List<String> illegalOrphanMessages = null;
            for (Device deviceListOldDevice : deviceListOld) {
                if (!deviceListNew.contains(deviceListOldDevice)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Device " + deviceListOldDevice + " since its roomId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Device> attachedDeviceListNew = new ArrayList<Device>();
            for (Device deviceListNewDeviceToAttach : deviceListNew) {
                deviceListNewDeviceToAttach = em.getReference(deviceListNewDeviceToAttach.getClass(), deviceListNewDeviceToAttach.getId());
                attachedDeviceListNew.add(deviceListNewDeviceToAttach);
            }
            deviceListNew = attachedDeviceListNew;
            room.setDeviceList(deviceListNew);
            room = em.merge(room);
            for (Device deviceListNewDevice : deviceListNew) {
                if (!deviceListOld.contains(deviceListNewDevice)) {
                    Room oldRoomIdOfDeviceListNewDevice = deviceListNewDevice.getRoomId();
                    deviceListNewDevice.setRoomId(room);
                    deviceListNewDevice = em.merge(deviceListNewDevice);
                    if (oldRoomIdOfDeviceListNewDevice != null && !oldRoomIdOfDeviceListNewDevice.equals(room)) {
                        oldRoomIdOfDeviceListNewDevice.getDeviceList().remove(deviceListNewDevice);
                        oldRoomIdOfDeviceListNewDevice = em.merge(oldRoomIdOfDeviceListNewDevice);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = room.getId();
                if (findRoom(id) == null) {
                    throw new NonexistentEntityException("The room with id " + id + " no longer exists.");
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
            Room room;
            try {
                room = em.getReference(Room.class, id);
                room.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The room with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Device> deviceListOrphanCheck = room.getDeviceList();
            for (Device deviceListOrphanCheckDevice : deviceListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Room (" + room + ") cannot be destroyed since the Device " + deviceListOrphanCheckDevice + " in its deviceList field has a non-nullable roomId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(room);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Room> findRoomEntities() {
        return findRoomEntities(true, -1, -1);
    }

    public List<Room> findRoomEntities(int maxResults, int firstResult) {
        return findRoomEntities(false, maxResults, firstResult);
    }

    private List<Room> findRoomEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Room.class));
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

    public Room findRoom(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Room.class, id);
        } finally {
            em.close();
        }
    }

    public int getRoomCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Room> rt = cq.from(Room.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
