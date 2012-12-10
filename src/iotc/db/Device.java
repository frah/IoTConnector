package iotc.db;

import iotc.type.DeviceType;
import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.itolab.morihit.clinkx.UPnPRemoteDevice;

/**
 * デバイスのエンティティ
 * @author atsushi-o
 */
@Entity
@EntityListeners(iotc.event.JpaEventListener.class)
@Table(name = "device", catalog = "iotc", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Device.findAll", query = "SELECT d FROM Device d"),
    @NamedQuery(name = "Device.findById", query = "SELECT d FROM Device d WHERE d.id = :id"),
    @NamedQuery(name = "Device.findByName", query = "SELECT d FROM Device d WHERE d.name = :name"),
    @NamedQuery(name = "Device.findByUDN", query = "SELECT d FROM Device d WHERE d.udn = :udn"),
    @NamedQuery(name = "Device.findByExplanation", query = "SELECT d FROM Device d WHERE d.explanation = :explanation")})
public class Device implements Serializable {
    @Basic(optional = false)
    @Column(name = "UDN", nullable = false, length = 255)
    private String udn;
    @Basic(optional = false)
    @Column(name = "type", nullable = false)
    private int type;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "name", nullable = false, length = 255)
    private String name;
    @Column(name = "explanation", length = 255)
    private String explanation;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "deviceId")
    private List<Sensor> sensorList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "deviceId")
    private List<Command> commandList;
    @JoinColumn(name = "room_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Room roomId;

    public Device() {
    }

    public Device(Integer id) {
        this.id = id;
    }

    public Device(Integer id, String name, String UDN) {
        this.id = id;
        this.name = name;
        this.udn = UDN;
    }

    public Device(UPnPRemoteDevice upprd) {
        this.name = upprd.getFriendlyName();
        this.udn = upprd.getUDN();
        if (upprd.getFriendlyName().startsWith("SunSPOT")) {
            this.type = DeviceType.SunSPOT.getId();
        } else {
            this.type = DeviceType.Other.getId();
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    @XmlTransient
    public List<Sensor> getSensorList() {
        return sensorList;
    }

    public void setSensorList(List<Sensor> sensorList) {
        this.sensorList = sensorList;
    }

    @XmlTransient
    public List<Command> getCommandList() {
        return commandList;
    }

    public void setCommandList(List<Command> commandList) {
        this.commandList = commandList;
    }

    public Room getRoomId() {
        return roomId;
    }

    public void setRoomId(Room roomId) {
        this.roomId = roomId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Device)) {
            return false;
        }
        Device other = (Device) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "iotc.db.Device[ id=" + id + " ]";
    }

    public String getUdn() {
        return udn;
    }

    public void setUdn(String udn) {
        this.udn = udn;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
