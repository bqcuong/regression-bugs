package edu.harvard.pallmall;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Location {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private String hospitalName;
	private String wardName;
	

	protected Location() {}
	
	public Location(String hospitalName, String wardName) {
		this.hospitalName = hospitalName;
		this.wardName     = wardName;
	}
	
	
	
	public String getHospitalName() {
		return hospitalName;
	}

	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}

	public String getWardName() {
		return wardName;
	}

	public void setWardName(String wardName) {
		this.wardName = wardName;
	}

	
	
	

}
