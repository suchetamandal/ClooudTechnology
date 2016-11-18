package com.sjsu486;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "employee")
public class EmployeeConverter {

	private Employee entity = null;

	public EmployeeConverter() {
		entity = new Employee();
	}

	public EmployeeConverter(Employee entity) {
		this.entity = entity;
	}

	@XmlElement
	public String getFirstName() {
		return entity.getFirstName();
	}

	@XmlElement
	public String getLastName() {
		return entity.getLastName();
	}
	
	@XmlElement
	public int getId() {
		return entity.getId();
	}

	public Employee getEmployee() {
		return entity;
	}

	public void setFirstName(String firstName) {
		entity.setFirstName(firstName);
	}
	
	public void setId(int id) {
		entity.setId(id);
	}

	public void setLastName(String lastName) {
		entity.setLastName(lastName);
	}

}