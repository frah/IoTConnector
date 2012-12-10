package iotc.db;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * デバイスのコマンドのエンティティ
 * @author atsushi-o
 */
@Entity
@EntityListeners(iotc.event.JpaEventListener.class)
@Table(name = "command", catalog = "iotc", schema = "", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"alias_name"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Command.findAll", query = "SELECT c FROM Command c"),
    @NamedQuery(name = "Command.findById", query = "SELECT c FROM Command c WHERE c.id = :id"),
    @NamedQuery(name = "Command.findByName", query = "SELECT c FROM Command c WHERE c.name = :name"),
    @NamedQuery(name = "Command.findByType", query = "SELECT c FROM Command c WHERE c.type = :type"),
    @NamedQuery(name = "Command.findByPower", query = "SELECT c FROM Command c WHERE c.power = :power"),
    @NamedQuery(name = "Command.findByAliasName", query = "SELECT c FROM Command c WHERE c.aliasName = :aliasName")})
public class Command implements Serializable {
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
    @Column(name = "type", nullable = false)
    private int type;
    @Basic(optional = false)
    @Column(name = "power", nullable = false)
    private int power;
    @Basic(optional = false)
    @Lob
    @Column(name = "command", nullable = false, length = 65535)
    private String command;
    @Column(name = "alias_name", length = 255)
    private String aliasName;
    @ManyToMany(mappedBy = "commandList")
    private List<Task> taskList;
    @OneToMany(mappedBy = "comId")
    private List<Log> logList;
    @JoinColumn(name = "device_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Device deviceId;

    public Command() {
    }

    public Command(Integer id) {
        this.id = id;
    }

    public Command(Integer id, String name, int type, int power, String command) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.power = power;
        this.command = command;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    @XmlTransient
    public List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }

    @XmlTransient
    public List<Log> getLogList() {
        return logList;
    }

    public void setLogList(List<Log> logList) {
        this.logList = logList;
    }

    public Device getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Device deviceId) {
        this.deviceId = deviceId;
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
        if (!(object instanceof Command)) {
            return false;
        }
        Command other = (Command) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "iotc.db.Command[ id=" + id + " ]";
    }

}
