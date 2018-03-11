package edu.harvard.h2ms.domain.core;

import javax.persistence.*;

/**
 * A Location is..
 */
@Entity
@Table(name = "LOCATION")
public class Location {

	/* Properties */
	private Long id;
	private String hospitalName;
	private String wardName;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "ID")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "HOSPITAL_NAME")
	public String getHospitalName() {
		return hospitalName;
	}

	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}

	@Column(name = "WARD_NAME")
	public String getWardName() {
		return wardName;
	}

	public void setWardName(String wardName) {
		this.wardName = wardName;
	}

	@Override
	public String toString() {
		return "Location - Id: " + id + ", Hospital Name: " + hospitalName + ", Ward Name: " + wardName;
	}

}
