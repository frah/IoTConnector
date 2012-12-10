package iotc.db;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * 条件式のエンティティ
 * @author atsushi-o
 */
@Entity
@EntityListeners(iotc.event.JpaEventListener.class)
@Table(name = "term", catalog = "iotc", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Term.findAll", query = "SELECT t FROM Term t"),
    @NamedQuery(name = "Term.findById", query = "SELECT t FROM Term t WHERE t.id = :id")})
public class Term implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @Lob
    @Column(name = "term", nullable = false, length = 65535)
    private String term;
    @JoinTable(name = "ref_term_sensor", joinColumns = {
        @JoinColumn(name = "term", referencedColumnName = "id", nullable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "sensor", referencedColumnName = "id", nullable = false)})
    @ManyToMany
    private List<Sensor> sensorList;
    @JoinTable(name = "ref_term_method", joinColumns = {
        @JoinColumn(name = "term", referencedColumnName = "id", nullable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "method", referencedColumnName = "id", nullable = false)})
    @ManyToMany
    private List<StatisticalMethod> statisticalMethodList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "term")
    private List<Task> taskList;

    public Term() {
    }

    public Term(Integer id) {
        this.id = id;
    }

    public Term(Integer id, String term) {
        this.id = id;
        this.term = term;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    @XmlTransient
    public List<Sensor> getSensorList() {
        return sensorList;
    }

    public void setSensorList(List<Sensor> sensorList) {
        this.sensorList = sensorList;
    }

    @XmlTransient
    public List<StatisticalMethod> getStatisticalMethodList() {
        return statisticalMethodList;
    }

    public void setStatisticalMethodList(List<StatisticalMethod> statisticalMethodList) {
        this.statisticalMethodList = statisticalMethodList;
    }

    @XmlTransient
    public List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
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
        if (!(object instanceof Term)) {
            return false;
        }
        Term other = (Term) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "iotc.db.Term[ id=" + id + " ]";
    }

}
