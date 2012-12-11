package iotc.db;
// Generated 2012/12/11 18:19:43 by Hibernate Tools 3.2.1.GA


import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Room generated by hbm2java
 */
@Entity
@Table(name="room"
    ,catalog="iotc"
)
public class Room  implements java.io.Serializable {


     private Integer id;
     private String name;
     private String explanation;
     private Set devices = new HashSet(0);

    public Room() {
    }


    public Room(String name) {
        this.name = name;
    }
    public Room(String name, String explanation, Set devices) {
       this.name = name;
       this.explanation = explanation;
       this.devices = devices;
    }

     @Id @GeneratedValue(strategy=IDENTITY)

    @Column(name="id", unique=true, nullable=false)
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name="name", nullable=false)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name="explanation")
    public String getExplanation() {
        return this.explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="room")
    public Set getDevices() {
        return this.devices;
    }

    public void setDevices(Set devices) {
        this.devices = devices;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
