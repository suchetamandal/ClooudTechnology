package com.sjsu486;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "project")
public class Project {
	private String name;
	private float budget;
	private int id;

	public Project(String name, float budget, int id) {
		this.name = name;
		this.budget = budget;
		this.id = id;
	}

	public Project() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getBudget() {
		return budget;
	}

	public void setBudget(float budget) {
		this.budget = budget;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
