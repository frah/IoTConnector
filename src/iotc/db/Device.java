package iotc.db;
// Generated 2012/12/11 18:19:43 by Hibernate Tools 3.2.1.GA


import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.itolab.morihit.clinkx.UPnPRemoteDevice;

/**
 * Device generated by hbm2java
 */
@Entity
@EntityListeners(iotc.event.HibernateEventListener.class)
@Table(name="device"
    ,catalog="iotc"
)
public class Device  implements java.io.Serializable {


     private Integer id;
     private Room room;
     private String name;
     private String udn;
     private int type;
     private String explanation;
     private Set commands = new HashSet(0);
     private Set sensors = new HashSet(0);
     private String aliasName;

    public Device() {
    }


    public Device(Room room, String name, String udn, int type) {
        this.room = room;
        this.name = name;
        this.udn = udn;
        this.type = type;
    }
    public Device(Room room, String name, String udn, int type, String explanation, Set commands, Set sensors) {
       this.room = room;
       this.name = name;
       this.udn = udn;
       this.type = type;
       this.explanation = explanation;
       this.commands = commands;
       this.sensors = sensors;
    }
    public Device(UPnPRemoteDevice upprd) {
        this.name = upprd.getFriendlyName();
        this.udn = upprd.getUDN();
        if (upprd.getFriendlyName().startsWith("SunSPOT")) {
            this.type = DeviceType.SunSPOT.getId();
        } else {
            this.type = DeviceType.OtherUPnP.getId();
        }
    }

     @Id @GeneratedValue(strategy=IDENTITY)

    @Column(name="id", unique=true, nullable=false)
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="room_id", nullable=false)
    public Room getRoom() {
        return this.room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    @Column(name="name", nullable=false)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name="UDN", nullable=false)
    public String getUdn() {
        return this.udn;
    }

    public void setUdn(String udn) {
        this.udn = udn;
    }

    @Column(name="type", nullable=false)
    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Column(name="explanation")
    public String getExplanation() {
        return this.explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="device")
    public Set getCommands() {
        return this.commands;
    }

    public void setCommands(Set commands) {
        this.commands = commands;
    }
@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="device")
    public Set getSensors() {
        return this.sensors;
    }

    public void setSensors(Set sensors) {
        this.sensors = sensors;
    }

    @Column(name="alias_name")
    public String getAliasName() {
        return this.aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
