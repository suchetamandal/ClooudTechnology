package com.sjsu486;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "projectList")
public class ProjectList {
	private List<ProjectConverter> list;

	public ProjectList() {
		list = new ArrayList<ProjectConverter>();
	}

	public void add(ProjectConverter p) {
		list.add(p);
	}
}
