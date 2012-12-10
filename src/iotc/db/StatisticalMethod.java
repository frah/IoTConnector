package iotc.db;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * 統計関数のエンティティ
 * @author atsushi-o
 */
@Entity
@EntityListeners(iotc.event.JpaEventListener.class)
@Table(name = "statistical_method", catalog = "iotc", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "StatisticalMethod.findAll", query = "SELECT s FROM StatisticalMethod s"),
    @NamedQuery(name = "StatisticalMethod.findById", query = "SELECT s FROM StatisticalMethod s WHERE s.id = :id"),
    @NamedQuery(name = "StatisticalMethod.findByTimespan", query = "SELECT s FROM StatisticalMethod s WHERE s.timespan = :timespan")})
public class StatisticalMethod implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @Lob
    @Column(name = "method", nullable = false, length = 65535)
    private String method;
    @Basic(optional = false)
    @Column(name = "timespan", nullable = false)
    private int timespan;
    @JoinTable(name = "ref_method_sensor", joinColumns = {
        @JoinColumn(name = "method", referencedColumnName = "id", nullable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "sensor", referencedColumnName = "id", nullable = false)})
    @ManyToMany
    private List<Sensor> sensorList;
    @ManyToMany(mappedBy = "statisticalMethodList")
    private List<Term> termList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "method")
    private List<StatisticValue> statisticValueList;

    public StatisticalMethod() {
    }

    public StatisticalMethod(Integer id) {
        this.id = id;
    }

    public StatisticalMethod(Integer id, String method, int timespan) {
        this.id = id;
        this.method = method;
        this.timespan = timespan;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getTimespan() {
        return timespan;
    }

    public void setTimespan(int timespan) {
        this.timespan = timespan;
    }

    @XmlTransient
    public List<Sensor> getSensorList() {
        return sensorList;
    }

    public void setSensorList(List<Sensor> sensorList) {
        this.sensorList = sensorList;
    }

    @XmlTransient
    public List<Term> getTermList() {
        return termList;
    }

    public void setTermList(List<Term> termList) {
        this.termList = termList;
    }

    @XmlTransient
    public List<StatisticValue> getStatisticValueList() {
        return statisticValueList;
    }

    public void setStatisticValueList(List<StatisticValue> statisticValueList) {
        this.statisticValueList = statisticValueList;
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
        if (!(object instanceof StatisticalMethod)) {
            return false;
        }
        StatisticalMethod other = (StatisticalMethod) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "iotc.db.StatisticalMethod[ id=" + id + " ]";
    }

}
