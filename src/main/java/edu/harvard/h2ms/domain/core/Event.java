package edu.harvard.h2ms.domain.core;

import java.sql.Time;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * An Event is what observer or sensor records about observee's actions.
 */
@Entity
@Table(name = "EVENT")
public class Event {

    /* Properties */

	@Id
	@GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

	@Column(name = "TIMESTAMP")
    private Date timestamp;

	@Column(name = "METHOD_ID")
    private Long method_id;

	@Column(name = "RELATIVE_MOMENT_ID")
    private Long relativeMoment;

	@Column(name = "OBSERVEE")
    private String observee;

	@Column(name = "OBSERVER")
    private String observer;

	@Column(name = "OBSERVATION_TYPE")
    private String observationType;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date date) {
        this.timestamp = date;
    }


    public Long getMethod() {
        return method_id;
    }

    public void setMethod(Long method_id) {
        this.method_id = method_id;
    }


    public Long getRelativeMoment() {
        return relativeMoment;
    }

    public void setRelativeMoment(RelativeMoment relativeMoment) {
        this.relativeMoment = relativeMoment.getId();
    }


    public String getObservee() {
        return observee;
    }

    public void setObservee(String observee) {
        this.observee = observee;
    }


    public String getObserver() {
        return observer;
    }

    public void setObserver(String observer) {
        this.observer = observer;
    }


    public String getObservationType() {
        return observationType;
    }

    public void setObservationType(String observationType) {
        this.observationType = observationType;
    }

    @Override
    public String toString() {
        return "Event - Id: " + id + ", Typestamp: " + timestamp
                + ", Hand Wash Type: " + method_id  + ", Relative Moment: " + relativeMoment
                + ", Observee: " + observee  + ", Observer: " + observer  + ", Observation Type: " + observationType;
    }

}
