package edu.harvard.pallmall.domain;

import javax.persistence.*;

/**
 * An Event is...
 */
@Entity
@Table(name = "EVENT")
public class Event {

    /* Properties */
    private Long id;
    private String formName;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "ID")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "FORM_NAME")
    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    @Override
    public String toString() {
        return "Event - Id: " + id + ", Form Name: " + formName;
    }

}
