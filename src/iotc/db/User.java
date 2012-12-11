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
 * User generated by hbm2java
 */
@Entity
@Table(name="user"
    ,catalog="iotc"
)
public class User  implements java.io.Serializable {


     private Integer id;
     private String name;
     private String aliasName;
     private Set logs = new HashSet(0);
     private Set powersForAuthBy = new HashSet(0);
     private Set powersForUserId = new HashSet(0);

    public User() {
    }

	
    public User(String name) {
        this.name = name;
    }
    public User(String name, String aliasName, Set logs, Set powersForAuthBy, Set powersForUserId) {
       this.name = name;
       this.aliasName = aliasName;
       this.logs = logs;
       this.powersForAuthBy = powersForAuthBy;
       this.powersForUserId = powersForUserId;
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
    
    @Column(name="alias_name", length=65535)
    public String getAliasName() {
        return this.aliasName;
    }
    
    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }
@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="user")
    public Set getLogs() {
        return this.logs;
    }
    
    public void setLogs(Set logs) {
        this.logs = logs;
    }
@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="userByAuthBy")
    public Set getPowersForAuthBy() {
        return this.powersForAuthBy;
    }
    
    public void setPowersForAuthBy(Set powersForAuthBy) {
        this.powersForAuthBy = powersForAuthBy;
    }
@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="userByUserId")
    public Set getPowersForUserId() {
        return this.powersForUserId;
    }
    
    public void setPowersForUserId(Set powersForUserId) {
        this.powersForUserId = powersForUserId;
    }




}


