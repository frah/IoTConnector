package iotc.db;
// Generated 2012/12/11 18:19:43 by Hibernate Tools 3.2.1.GA


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Power generated by hbm2java
 */
@Entity
@EntityListeners(iotc.event.JpaEventListener.class)
@Table(name="power"
    ,catalog="iotc"
)
public class Power  implements java.io.Serializable {


     private int userId;
     private User userByUserId;
     private User userByAuthBy;
     private int power;
     private Integer prevPower;
     private int type;
     private Integer period;

    public Power() {
    }


    public Power(int userId, User userByUserId, int power, int type) {
        this.userId = userId;
        this.userByUserId = userByUserId;
        this.power = power;
        this.type = type;
    }
    public Power(int userId, User userByUserId, User userByAuthBy, int power, Integer prevPower, int type, Integer period) {
       this.userId = userId;
       this.userByUserId = userByUserId;
       this.userByAuthBy = userByAuthBy;
       this.power = power;
       this.prevPower = prevPower;
       this.type = type;
       this.period = period;
    }

     @Id

    @Column(name="user_id", unique=true, nullable=false)
    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", unique=true, nullable=false, insertable=false, updatable=false)
    public User getUserByUserId() {
        return this.userByUserId;
    }

    public void setUserByUserId(User userByUserId) {
        this.userByUserId = userByUserId;
    }
@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="auth_by")
    public User getUserByAuthBy() {
        return this.userByAuthBy;
    }

    public void setUserByAuthBy(User userByAuthBy) {
        this.userByAuthBy = userByAuthBy;
    }

    @Column(name="power", nullable=false)
    public int getPower() {
        return this.power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    @Column(name="prev_power")
    public Integer getPrevPower() {
        return this.prevPower;
    }

    public void setPrevPower(Integer prevPower) {
        this.prevPower = prevPower;
    }

    @Column(name="type", nullable=false)
    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Column(name="period")
    public Integer getPeriod() {
        return this.period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }




}


