package edu.harvard.h2ms.domain.core;

import javax.persistence.*;

/**
 * A Method is a hand washing method, such as soap and water, or sanitizer.
 */
@Entity
@Table(name = "METHOD")
public class Method {

	/* Properties */
	private Long id;
	private String name;
	private String description;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "ID")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Method - Id: " + id + ", Name: " + name + ", Description: " + description;
	}
}
