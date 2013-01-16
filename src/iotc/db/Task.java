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
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Task generated by hbm2java
 */
@Entity
@EntityListeners(iotc.event.HibernateEventListener.class)
@Table(name="task"
    ,catalog="iotc"
)
public class Task  implements java.io.Serializable {


     private Integer id;
     private Term term;
     private String name;
     private Set commands = new HashSet(0);

    public Task() {
    }


    public Task(Term term, String name) {
        this.term = term;
        this.name = name;
    }
    public Task(Term term, String name, Set commands) {
       this.term = term;
       this.name = name;
       this.commands = commands;
    }

     @Id @GeneratedValue(strategy=IDENTITY)

    @Column(name="id", unique=true, nullable=false)
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="term", nullable=false)
    public Term getTerm() {
        return this.term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    @Column(name="name", nullable=false)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
@ManyToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    @JoinTable(name="ref_task_command", catalog="iotc", joinColumns = {
        @JoinColumn(name="task", nullable=false, updatable=false) }, inverseJoinColumns = {
        @JoinColumn(name="command", nullable=false, updatable=false) })
    public Set getCommands() {
        return this.commands;
    }

    public void setCommands(Set commands) {
        this.commands = commands;
    }




}


