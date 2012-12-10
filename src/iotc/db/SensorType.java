package iotc.db;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * センサ種類のエンティティ
 * @author atsushi-o
 */
@Entity
@EntityListeners(iotc.event.JpaEventListener.class)
@Table(name = "sensor_type", catalog = "iotc", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SensorType.findAll", query = "SELECT s FROM SensorType s"),
    @NamedQuery(name = "SensorType.findById", query = "SELECT s FROM SensorType s WHERE s.id = :id"),
    @NamedQuery(name = "SensorType.findByName", query = "SELECT s FROM SensorType s WHERE s.name = :name"),
    @NamedQuery(name = "SensorType.findByUnit", query = "SELECT s FROM SensorType s WHERE s.unit = :unit")})
public class SensorType implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "name", nullable = false, length = 255)
    private String name;
    @Basic(optional = false)
    @Column(name = "unit", nullable = false, length = 32)
    private String unit;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "type")
    private List<Sensor> sensorList;

    public SensorType() {
    }

    public SensorType(Integer id) {
        this.id = id;
    }

    public SensorType(Integer id, String name, String unit) {
        this.id = id;
        this.name = name;
        this.unit = unit;
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @XmlTransient
    public List<Sensor> getSensorList() {
        return sensorList;
    }

    public void setSensorList(List<Sensor> sensorList) {
        this.sensorList = sensorList;
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
        if (!(object instanceof SensorType)) {
            return false;
        }
        SensorType other = (SensorType) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "iotc.db.SensorType[ id=" + id + " ]";
    }

}
