package iotc.db;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * 操作ログのエンティティ
 * @author atsushi-o
 */
@Entity
@EntityListeners(iotc.event.JpaEventListener.class)
@Table(name = "log", catalog = "iotc", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Log.findAll", query = "SELECT l FROM Log l"),
    @NamedQuery(name = "Log.findById", query = "SELECT l FROM Log l WHERE l.id = :id"),
    @NamedQuery(name = "Log.findByState", query = "SELECT l FROM Log l WHERE l.state = :state")})
public class Log implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Lob
    @Column(name = "com_variable", length = 65535)
    private String comVariable;
    @Basic(optional = false)
    @Column(name = "state", nullable = false)
    private int state;
    @OneToMany(mappedBy = "relationalLog")
    private List<Log> logList;
    @JoinColumn(name = "relational_log", referencedColumnName = "id")
    @ManyToOne
    private Log relationalLog;
    @JoinColumn(name = "com_id", referencedColumnName = "id")
    @ManyToOne
    private Command comId;
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private User userId;

    public Log() {
    }

    public Log(Integer id) {
        this.id = id;
    }

    public Log(Integer id, int state) {
        this.id = id;
        this.state = state;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getComVariable() {
        return comVariable;
    }

    public void setComVariable(String comVariable) {
        this.comVariable = comVariable;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @XmlTransient
    public List<Log> getLogList() {
        return logList;
    }

    public void setLogList(List<Log> logList) {
        this.logList = logList;
    }

    public Log getRelationalLog() {
        return relationalLog;
    }

    public void setRelationalLog(Log relationalLog) {
        this.relationalLog = relationalLog;
    }

    public Command getComId() {
        return comId;
    }

    public void setComId(Command comId) {
        this.comId = comId;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
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
        if (!(object instanceof Log)) {
            return false;
        }
        Log other = (Log) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "iotc.db.Log[ id=" + id + " ]";
    }

}
