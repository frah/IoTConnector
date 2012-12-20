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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Term generated by hbm2java
 */
@Entity
@EntityListeners(iotc.event.JpaEventListener.class)
@Table(name="term"
    ,catalog="iotc"
)
public class Term  implements java.io.Serializable {


     private Integer id;
     private String term;
     private Set sensors = new HashSet(0);
     private Set statisticalMethods = new HashSet(0);
     private Set tasks = new HashSet(0);

    public Term() {
    }


    public Term(String term) {
        this.term = term;
    }
    public Term(String term, Set sensors, Set statisticalMethods, Set tasks) {
       this.term = term;
       this.sensors = sensors;
       this.statisticalMethods = statisticalMethods;
       this.tasks = tasks;
    }

     @Id @GeneratedValue(strategy=IDENTITY)

    @Column(name="id", unique=true, nullable=false)
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name="term", nullable=false, length=65535)
    public String getTerm() {
        return this.term;
    }

    public void setTerm(String term) {
        this.term = term;
    }
@ManyToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    @JoinTable(name="ref_term_sensor", catalog="iotc", joinColumns = {
        @JoinColumn(name="term", nullable=false, updatable=false) }, inverseJoinColumns = {
        @JoinColumn(name="sensor", nullable=false, updatable=false) })
    public Set getSensors() {
        return this.sensors;
    }

    public void setSensors(Set sensors) {
        this.sensors = sensors;
    }
@ManyToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    @JoinTable(name="ref_term_method", catalog="iotc", joinColumns = {
        @JoinColumn(name="term", nullable=false, updatable=false) }, inverseJoinColumns = {
        @JoinColumn(name="method", nullable=false, updatable=false) })
    public Set getStatisticalMethods() {
        return this.statisticalMethods;
    }

    public void setStatisticalMethods(Set statisticalMethods) {
        this.statisticalMethods = statisticalMethods;
    }
@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="term")
    public Set getTasks() {
        return this.tasks;
    }

    public void setTasks(Set tasks) {
        this.tasks = tasks;
    }




}


