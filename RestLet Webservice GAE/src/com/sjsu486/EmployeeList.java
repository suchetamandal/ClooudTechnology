package com.sjsu486;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "employeeList")
public class EmployeeList {
	private List<EmployeeConverter> list;

    public EmployeeList(){
        list = new ArrayList<EmployeeConverter>();
    }

    public void add(EmployeeConverter p){
        list.add(p);
    }
}
