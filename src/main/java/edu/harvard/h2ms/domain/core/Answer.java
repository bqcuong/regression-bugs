package edu.harvard.h2ms.domain.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import edu.harvard.h2ms.domain.core.*;

/**
 * A Answer
 */
@Entity
public class Answer {

    /* Properties */
	@Id
	@GeneratedValue(strategy= GenerationType.AUTO)
    @Column
    private Long id;

	@Column
    private Integer priority;

    @ManyToOne
    @JoinColumn(name="question_id")
    private Question question;

    @Column
    private String stringAnswer;

    @Column
    private Boolean boollAnswer;

    @Column
    private Integer valueAnswer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "event_id")
	private Event event;

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public String getStringAnswer() {
		return stringAnswer;
	}

	public void setStringAnswer(String stringAnswer) {
		this.stringAnswer = stringAnswer;
	}

	public Boolean getBoollAnswer() {
		return boollAnswer;
	}

	public void setBoollAnswer(Boolean boollAnswer) {
		this.boollAnswer = boollAnswer;
	}

	public Integer getValueAnswer() {
		return valueAnswer;
	}

	public void setValueAnswer(Integer valueAnswer) {
		this.valueAnswer = valueAnswer;
	}



}
