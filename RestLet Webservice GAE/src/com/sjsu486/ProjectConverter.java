package com.sjsu486;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "project")
public class ProjectConverter {
	private Project entity = null;

	public ProjectConverter() {
		entity = new Project();
	}

	public ProjectConverter(Project entity) {
		this.entity = entity;
	}

	@XmlElement
	public String getName() {
		return entity.getName();
	}

	@XmlElement
	public float getBudget() {
		return entity.getBudget();
	}

	@XmlElement
	public int getId() {
		return entity.getId();
	}

	public Project getProject() {
		return entity;
	}

	public void setName(String name) {
		entity.setName(name);
	}

	public void setId(int id) {
		entity.setId(id);
	}

	public void setBudget(float budget) {
		entity.setBudget(budget);
	}

}