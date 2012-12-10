package iotc.db;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * センサ値のエンティティ
 * @author atsushi-o
 */
@Entity
@EntityListeners(iotc.event.JpaEventListener.class)
@Table(name = "sensor_value", catalog = "iotc", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SensorValue.findAll", query = "SELECT s FROM SensorValue s"),
    @NamedQuery(name = "SensorValue.findById", query = "SELECT s FROM SensorValue s WHERE s.id = :id"),
    @NamedQuery(name = "SensorValue.findByTimestamp", query = "SELECT s FROM SensorValue s WHERE s.timestamp = :timestamp"),
    @NamedQuery(name = "SensorValue.findByValue", query = "SELECT s FROM SensorValue s WHERE s.value = :value")})
public class SensorValue implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "timestamp", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;
    @Basic(optional = false)
    @Column(name = "value", nullable = false)
    private double value;
    @JoinColumn(name = "sensor", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Sensor sensor;

    public SensorValue() {
    }

    public SensorValue(Integer id) {
        this.id = id;
    }

    public SensorValue(Integer id, Date timestamp, double value) {
        this.id = id;
        this.timestamp = timestamp;
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
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
        if (!(object instanceof SensorValue)) {
            return false;
        }
        SensorValue other = (SensorValue) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "iotc.db.SensorValue[ id=" + id + " ]";
    }

}
