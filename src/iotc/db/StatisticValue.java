package iotc.db;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 統計値のエンティティ
 * @author atsushi-o
 */
@Entity
@EntityListeners(iotc.event.JpaEventListener.class)
@Table(name = "statistic_value", catalog = "iotc", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "StatisticValue.findAll", query = "SELECT s FROM StatisticValue s"),
    @NamedQuery(name = "StatisticValue.findById", query = "SELECT s FROM StatisticValue s WHERE s.id = :id"),
    @NamedQuery(name = "StatisticValue.findByTimestamp", query = "SELECT s FROM StatisticValue s WHERE s.timestamp = :timestamp"),
    @NamedQuery(name = "StatisticValue.findByValue", query = "SELECT s FROM StatisticValue s WHERE s.value = :value")})
public class StatisticValue implements Serializable {
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
    @JoinColumn(name = "method", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private StatisticalMethod method;

    public StatisticValue() {
    }

    public StatisticValue(Integer id) {
        this.id = id;
    }

    public StatisticValue(Integer id, Date timestamp, double value) {
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

    public StatisticalMethod getMethod() {
        return method;
    }

    public void setMethod(StatisticalMethod method) {
        this.method = method;
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
        if (!(object instanceof StatisticValue)) {
            return false;
        }
        StatisticValue other = (StatisticValue) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "iotc.db.StatisticValue[ id=" + id + " ]";
    }

}
