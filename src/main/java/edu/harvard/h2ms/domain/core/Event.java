package edu.harvard.h2ms.domain.core;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;


/**
 * An Event is what observer or sensor records about observee's actions.
 */
@Entity
@Table(name = "EVENT")
public class Event {
	/* Properties */
	@Id
	@GeneratedValue(strategy= GenerationType.AUTO)
    @Column
    private Long id;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column
    private Date timestamp;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subject_id")
    private User subject;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "event_template_id")
    private EventTemplate eventTemplate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "observer_id")
    private User observer;

	@OneToMany(fetch = FetchType.LAZY,
			cascade = CascadeType.ALL,
			mappedBy = "event")
	private Set<Answer> answers = new HashSet<>();

	public Set<Answer> getAnswers() {
		return answers;
	}

	public void setAnswers(Set<Answer> answers) {	
		answers.forEach((a)->{a.setEvent(this);});
		
		this.answers = answers;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public User getSubject() {
		return subject;
	}

	public void setSubject(User subject) {
		this.subject = subject;
	}

	public User getObserver() {
		return observer;
	}

	public void setObserver(User observer) {
		this.observer = observer;
	}

	public EventTemplate getEventTemplate() {
		return eventTemplate;
	}

	public void setEventTemplate(EventTemplate eventTemplate) {
		this.eventTemplate = eventTemplate;
	}
	
    @Override
	public String toString() {
		return "Event [id=" + id + ", timestamp=" + timestamp + ", subject=" + subject + ", eventTemplate="
				+ eventTemplate + ", observer=" + observer + ", answers=" + answers + "]";
	}
}
