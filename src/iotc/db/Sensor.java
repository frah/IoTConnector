package iotc.db;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * センサのエンティティ
 * @author atsushi-o
 */
@Entity
@Table(name = "sensor", catalog = "iotc", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Sensor.findAll", query = "SELECT s FROM Sensor s"),
    @NamedQuery(name = "Sensor.findById", query = "SELECT s FROM Sensor s WHERE s.id = :id")})
public class Sensor implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @ManyToMany(mappedBy = "sensorList")
    private List<Term> termList;
    @ManyToMany(mappedBy = "sensorList")
    private List<StatisticalMethod> statisticalMethodList;
    @JoinColumn(name = "device_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Device deviceId;
    @JoinColumn(name = "type", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private SensorType type;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sensor")
    private List<SensorValue> sensorValueList;

    public Sensor() {
    }

    public Sensor(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @XmlTransient
    public List<Term> getTermList() {
        return termList;
    }

    public void setTermList(List<Term> termList) {
        this.termList = termList;
    }

    @XmlTransient
    public List<StatisticalMethod> getStatisticalMethodList() {
        return statisticalMethodList;
    }

    public void setStatisticalMethodList(List<StatisticalMethod> statisticalMethodList) {
        this.statisticalMethodList = statisticalMethodList;
    }

    public Device getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Device deviceId) {
        this.deviceId = deviceId;
    }

    public SensorType getType() {
        return type;
    }

    public void setType(SensorType type) {
        this.type = type;
    }

    @XmlTransient
    public List<SensorValue> getSensorValueList() {
        return sensorValueList;
    }

    public void setSensorValueList(List<SensorValue> sensorValueList) {
        this.sensorValueList = sensorValueList;
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
        if (!(object instanceof Sensor)) {
            return false;
        }
        Sensor other = (Sensor) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "iotc.db.Sensor[ id=" + id + " ]";
    }

}
