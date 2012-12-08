package iotc.db;

import java.io.Serializable;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * ユーザ権限のエンティティ
 * @author atsushi-o
 */
@Entity
@Table(name = "power", catalog = "iotc", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Power.findAll", query = "SELECT p FROM Power p"),
    @NamedQuery(name = "Power.findByUserId", query = "SELECT p FROM Power p WHERE p.userId = :userId"),
    @NamedQuery(name = "Power.findByPower", query = "SELECT p FROM Power p WHERE p.power = :power"),
    @NamedQuery(name = "Power.findByPrevPower", query = "SELECT p FROM Power p WHERE p.prevPower = :prevPower"),
    @NamedQuery(name = "Power.findByType", query = "SELECT p FROM Power p WHERE p.type = :type"),
    @NamedQuery(name = "Power.findByPeriod", query = "SELECT p FROM Power p WHERE p.period = :period")})
public class Power implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "user_id", nullable = false)
    private Integer userId;
    @Basic(optional = false)
    @Column(name = "power", nullable = false)
    private int power;
    @Column(name = "prev_power")
    private Integer prevPower;
    @Basic(optional = false)
    @Column(name = "type", nullable = false)
    private int type;
    @Column(name = "period")
    private Integer period;
    @JoinColumn(name = "auth_by", referencedColumnName = "id")
    @ManyToOne
    private User authBy;
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    @OneToOne(optional = false)
    private User user;

    public Power() {
    }

    public Power(Integer userId) {
        this.userId = userId;
    }

    public Power(Integer userId, int power, int type) {
        this.userId = userId;
        this.power = power;
        this.type = type;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public Integer getPrevPower() {
        return prevPower;
    }

    public void setPrevPower(Integer prevPower) {
        this.prevPower = prevPower;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public User getAuthBy() {
        return authBy;
    }

    public void setAuthBy(User authBy) {
        this.authBy = authBy;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userId != null ? userId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Power)) {
            return false;
        }
        Power other = (Power) object;
        if ((this.userId == null && other.userId != null) || (this.userId != null && !this.userId.equals(other.userId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "iotc.db.Power[ userId=" + userId + " ]";
    }

}
