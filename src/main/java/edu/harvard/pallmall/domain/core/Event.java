package edu.harvard.pallmall.domain.core;

import javax.persistence.*;
import java.sql.Time;

/**
 * An Event is...
 */
@Entity
@Table(name = "EVENT")
public class Event {

    /* Properties */
    private Long id;
    private Time timestamp;
    private String handWashType;
    private String relativeMoment;
    private String observee;
    private String observer;
    private String observationType;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "ID")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "TIMESTAMP")
    public Time getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Time timestamp) {
        this.timestamp = timestamp;
    }

    @Column(name = "HAND_WASH_TYPE")
    public String getHandWashType() {
        return handWashType;
    }

    public void setHandWashType(String handWashType) {
        this.handWashType = handWashType;
    }

    @Column(name = "RELATIVE_MOMENT")
    public String getRelativeMoment() {
        return relativeMoment;
    }

    public void setRelativeMoment(String relativeMoment) {
        this.relativeMoment = relativeMoment;
    }

    @Column(name = "OBSERVEE")
    public String getObservee() {
        return observee;
    }

    public void setObservee(String observee) {
        this.observee = observee;
    }

    @Column(name = "OBSERVER")
    public String getObserver() {
        return observer;
    }

    public void setObserver(String observer) {
        this.observer = observer;
    }

    @Column(name = "OBSERVATION_TYPE")
    public String getObservationType() {
        return observationType;
    }

    public void setObservationType(String observationType) {
        this.observationType = observationType;
    }

    @Override
    public String toString() {
        return "Event - Id: " + id + ", Typestamp: " + timestamp
                + ", Hand Wash Type: " + handWashType  + ", Relative Moment: " + relativeMoment
                + ", Observee: " + observee  + ", Observer: " + observer  + ", Observation Type: " + observationType;
    }

}
