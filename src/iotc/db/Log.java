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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Log generated by hbm2java
 */
@Entity
@Table(name="log"
    ,catalog="iotc"
)
public class Log  implements java.io.Serializable {


     private Integer id;
     private User user;
     private Log log;
     private Command command;
     private String comVariable;
     private int state;
     private Set logs = new HashSet(0);

    public Log() {
    }

	
    public Log(User user, int state) {
        this.user = user;
        this.state = state;
    }
    public Log(User user, Log log, Command command, String comVariable, int state, Set logs) {
       this.user = user;
       this.log = log;
       this.command = command;
       this.comVariable = comVariable;
       this.state = state;
       this.logs = logs;
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
    @JoinColumn(name="user_id", nullable=false)
    public User getUser() {
        return this.user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="relational_log")
    public Log getLog() {
        return this.log;
    }
    
    public void setLog(Log log) {
        this.log = log;
    }
@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="com_id")
    public Command getCommand() {
        return this.command;
    }
    
    public void setCommand(Command command) {
        this.command = command;
    }
    
    @Column(name="com_variable", length=65535)
    public String getComVariable() {
        return this.comVariable;
    }
    
    public void setComVariable(String comVariable) {
        this.comVariable = comVariable;
    }
    
    @Column(name="state", nullable=false)
    public int getState() {
        return this.state;
    }
    
    public void setState(int state) {
        this.state = state;
    }
@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="log")
    public Set getLogs() {
        return this.logs;
    }
    
    public void setLogs(Set logs) {
        this.logs = logs;
    }




}


