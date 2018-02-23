package edu.harvard.pallmall.domain;

import javax.persistence.*;

/**
 * A Doctor is...
 */
@Entity
@Table(name = "DOCTOR")
public class Doctor {

	/* Properties */
	private Long id;
	private String firstName;
	private String lastName;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "ID")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "FIRST_NAME")
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Column(name = "LAST_NAME")
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public String toString() {
		return "Doctor - Id: " + id + ", First Name: " + firstName + ", Last Name: " + lastName;
	}
}
