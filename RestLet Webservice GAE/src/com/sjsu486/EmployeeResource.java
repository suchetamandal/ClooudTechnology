package com.sjsu486;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.thoughtworks.xstream.XStream;

@Path("/employee")
public class EmployeeResource {
	
	private static final Logger log = Logger.getLogger(EmployeeResource.class.getName());
	
	@GET
	@Produces("application/xml")
	public Response getEmployees() {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		List<EmployeeConverter> employees = new ArrayList<EmployeeConverter>();

		Query pq = new Query("Employee");
		PreparedQuery preparedQ = datastore.prepare(pq);

		for (Entity emp : preparedQ.asIterable()) {
			Employee newEmp = new Employee();
			newEmp.setFirstName((String) emp.getProperty("firstName"));
			newEmp.setLastName((String) emp.getProperty("lastName"));
			newEmp.setId((int) (long) emp.getProperty("id"));
			EmployeeConverter employeeConverter = new EmployeeConverter(newEmp);
			employees.add(employeeConverter);
		}
		if (employees.size() > 0) {
			XStream xstream = new XStream();
			xstream.alias("employee", EmployeeConverter.class);
			xstream.addImplicitCollection(EmployeeList.class, "list");
			log.info("Found Employees.");

			EmployeeList list = new EmployeeList();
			for (EmployeeConverter ec : employees) {
				list.add(ec);
			}

			String xml = xstream.toXML(list);
			return Response.status(200).entity(xml).build();
		}
		log.warning("Found Employees.");
		return Response.status(404).build();
	}

	@GET
	@Produces("application/json")
	@Path("/json/")
	public Response getJSONEmployees() throws JSONException {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		ArrayList<JSONObject> employees = new ArrayList<JSONObject>();

		Query pq = new Query("Employee");
		PreparedQuery preparedQ = datastore.prepare(pq);

		for (Entity emp : preparedQ.asIterable()) {
			JSONObject newEmp = new JSONObject();
			newEmp.put("firstName", (String) emp.getProperty("firstName"));
			newEmp.put("lastName", (String) emp.getProperty("lastName"));
			newEmp.put("id", (int) (long) emp.getProperty("id"));

			employees.add(newEmp);
		}
		if(employees.size()>0){
		log.info("Found Employees.");
		}
		else{
			log.info("No Employees.");
		}
		return Response.status(200).entity(employees.toString()).build();

	}

	@GET
	@Produces("application/xml")
	@Path("/{id}/")
	public Response getEmployee(@PathParam("id") int id) {
		Entity foundEmployee = findEmployee(id);

		if (foundEmployee != null) {
			Employee newEmp = new Employee();
			newEmp.setFirstName((String) foundEmployee.getProperty("firstName"));
			newEmp.setLastName((String) foundEmployee.getProperty("lastName"));
			newEmp.setId((int) (long) foundEmployee.getProperty("id"));
			EmployeeConverter employeeConverter = new EmployeeConverter(newEmp);
			log.info("Found Employee.");
			return Response.status(200).entity(employeeConverter).build();
		} else {
			log.info("No Employee.");
			return Response.status(404).build();
		}

	}

	@GET
	@Produces("application/json")
	@Path("/json/{id}/")
	public Response getJSONEmployee(@PathParam("id") int id) throws JSONException {
		Entity foundEmployee = findEmployee(id);

		if (foundEmployee != null) {
			JSONObject newEmp = new JSONObject();
			newEmp.put("firstName", (String) foundEmployee.getProperty("firstName"));
			newEmp.put("lastName", (String) foundEmployee.getProperty("lastName"));
			newEmp.put("id", (long) foundEmployee.getProperty("id"));
			log.info("Found Employee.");
			return Response.status(200).entity(newEmp.toString()).build();
		} else {
			log.info("No Employee.");
			return Response.status(404).build();
		}

	}

	@POST
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response addEmployee(Employee employee) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Entity newEmployee = new Entity("Employee");
		Entity oldEmployee = findEmployee(employee.getId());
		if (oldEmployee != null) {
			log.info("One employee is already exist with same id");
			return Response.status(409).build();
		}
		newEmployee.setProperty("firstName", employee.getFirstName());
		newEmployee.setProperty("lastName", employee.getLastName());
		newEmployee.setProperty("id", employee.getId());

		datastore.put(newEmployee);
		String location = "http:2-dot-cmpe281suchetamandalhw486.appspot.com/cmpe281SuchetaMandal486/rest/employee/"
				+ employee.getId();
		String jsonlocation = "http:2-dot-cmpe281suchetamandalhw486.appspot.com/cmpe281SuchetaMandal486/rest/employee/json/"
				+ employee.getId();
		log.info("One Employee is added.");
		return Response.status(201).header("location", location).header("jsonlocation", jsonlocation).build();
	}

	@PUT
	@Consumes({ "application/xml", "application/json" })
	@Path("/{id}/")
	public Response updateEmployee(Employee employee,@PathParam("id") int id) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		if (employee != null) {
			Entity foundEmp = findEmployee(id);
			if (foundEmp != null) {
				if (employee.getFirstName() != null) {
					foundEmp.setProperty("firstName", employee.getFirstName());
				}
				if (employee.getLastName() != null) {
					foundEmp.setProperty("lastName", employee.getLastName());
				}
				datastore.put(foundEmp);
				log.info("One Employee details is updated.");
				return Response.status(200).build();
			}
		}
		log.info("No Employee or mallformed URL");
		return Response.status(404).build();
	}

	@DELETE
	@Path("/{id}/")
	public Response deleteEmployee(@PathParam("id") int id) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Entity foundEmployee = findEmployee(id);
		if (foundEmployee != null) {
			datastore.delete(foundEmployee.getKey());
			log.info("One Employee is deleted.");
			return Response.status(200).build();
		} else {
			log.info("No Employee is Found.");
			return Response.status(404).build();
		}

	}

	private Entity findEmployee(int id) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Filter idFilter = new FilterPredicate("id", FilterOperator.EQUAL, id);
		Query findEmp = new Query("Employee").setFilter(idFilter);

		PreparedQuery pq = datastore.prepare(findEmp);
		for (Entity emp : pq.asIterable()) {
			return emp;
		}
		return null;
	}

}
