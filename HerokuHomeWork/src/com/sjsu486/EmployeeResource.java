package com.sjsu486;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

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

import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

@Path("/employee")
public class EmployeeResource {

	@GET
	@Produces("application/xml")
	public List<EmployeeConverter> getEmployees() {
		List<EmployeeConverter> employees = new ArrayList<EmployeeConverter>();
		String dbURI = "mongodb://sucheta:sucheta@ds147777.mlab.com:47777/cmpe281db";
		MongoClient mongoClient = new MongoClient(new MongoClientURI(dbURI));
		DB db = mongoClient.getDB("cmpe281db");

		DBCollection empCollection = db.getCollection("employee");
		DBCursor cursor = empCollection.find();
		while (cursor.hasNext()) {
			DBObject emp = cursor.next();
			Employee newEmp = new Employee();
			newEmp.setFirstName((String) emp.get("firstName"));
			newEmp.setLastName((String) emp.get("lastName"));
			newEmp.setId((int) emp.get("_id"));
			EmployeeConverter employeeConverter = new EmployeeConverter(newEmp);
			employees.add(employeeConverter);
		}
		return employees;
	}

	@GET
	@Produces("application/json")
	@Path("/json/")
	public Response getJSONEmployees() throws JSONException {
		String dbURI = "mongodb://sucheta:sucheta@ds147777.mlab.com:47777/cmpe281db";
		MongoClient mongoClient = new MongoClient(new MongoClientURI(dbURI));
		DB db = mongoClient.getDB("cmpe281db");

		DBCollection empCollection = db.getCollection("employee");
		DBCursor cursor = empCollection.find();
		List<JSONObject> employees = new ArrayList<JSONObject>();

		while (cursor.hasNext()) {
			DBObject emp = cursor.next();
			JSONObject newEmp = new JSONObject();
			newEmp.put("firstName", (String) emp.get("firstName"));
			newEmp.put("lastName", (String) emp.get("lastName"));
			newEmp.put("id", (int) emp.get("_id"));
			employees.add(newEmp);
		}
		return Response.status(200).entity(employees.toString()).build();
	}

	@GET
	@Produces("application/xml")
	@Path("/{id}/")
	public Response getEmployee(@PathParam("id") int id) {
		DBObject query = BasicDBObjectBuilder.start().add("_id", id).get();
		String dbURI = "mongodb://sucheta:sucheta@ds147777.mlab.com:47777/cmpe281db";
		MongoClient mongoClient = new MongoClient(new MongoClientURI(dbURI));
		DB db = mongoClient.getDB("cmpe281db");
		DBCollection col = db.getCollection("employee");
		DBCursor cursor = col.find(query);
		DBObject foundEmployee = null;
		if (cursor != null && cursor.hasNext()) {
			foundEmployee = cursor.next();
		}

		if (foundEmployee != null) {
			Employee newEmp = new Employee();
			newEmp.setFirstName((String) foundEmployee.get("firstName"));
			newEmp.setLastName((String) foundEmployee.get("lastName"));
			newEmp.setId((int) foundEmployee.get("_id"));
			EmployeeConverter employeeConverter = new EmployeeConverter(newEmp);
			return Response.status(200).entity(employeeConverter).build();
		} else {
			return Response.status(404).build();
		}
	}

	@GET
	@Produces("application/json")
	@Path("/json/{id}/")
	public Response getJSONEmployee(@PathParam("id") int id) throws JSONException {
		DBObject query = BasicDBObjectBuilder.start().add("_id", id).get();
		String dbURI = "mongodb://sucheta:sucheta@ds147777.mlab.com:47777/cmpe281db";
		MongoClient mongoClient = new MongoClient(new MongoClientURI(dbURI));
		DB db = mongoClient.getDB("cmpe281db");
		DBCollection col = db.getCollection("employee");
		DBCursor cursor = col.find(query);
		DBObject foundEmployee = null;
		if (cursor != null && cursor.hasNext()) {
			foundEmployee = cursor.next();
		}

		if (foundEmployee != null) {
			JSONObject newEmp = new JSONObject();
			newEmp.put("firstName", (String) foundEmployee.get("firstName"));
			newEmp.put("lastName", (String) foundEmployee.get("lastName"));
			newEmp.put("id", (int) foundEmployee.get("_id"));
			return Response.status(200).entity(newEmp.toString()).build();
		} else {
			return Response.status(404).build();
		}
	}

	@POST
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response addEmployee(Employee employee) throws UnknownHostException {
		String dbURI = "mongodb://sucheta:sucheta@ds147777.mlab.com:47777/cmpe281db";
		MongoClient mongoClient = new MongoClient(new MongoClientURI(dbURI));
		DB db = mongoClient.getDB("cmpe281db");
		DBObject query = BasicDBObjectBuilder.start().add("_id", employee.getId()).get();
		DBCollection collection = db.getCollection("employee");
		DBCursor cursor = collection.find(query);
		DBObject foundEmployee = null;
		if (cursor != null && cursor.hasNext()) {
			foundEmployee = cursor.next();
		}
		if (foundEmployee != null) {
			mongoClient.close();
			return Response.status(409).build();
		}
		DBObject dbEmployee = createDBObject(employee);

		collection.insert(dbEmployee);
		String location = "http://localhost:8888/rest/employee/" + employee.getId();
		mongoClient.close();
		return Response.status(201).header("location", location).build();
	}

	private static DBObject createDBObject(Employee employee) {
		BasicDBObjectBuilder docBuilder = BasicDBObjectBuilder.start();

		docBuilder.append("_id", employee.getId());
		docBuilder.append("firstName", employee.getFirstName());
		docBuilder.append("lastName", employee.getLastName());
		return docBuilder.get();
	}

	@PUT
	@Consumes({ "application/xml", "application/json" })
	@Path("/{id}/")
	public Response updateEmployee(Employee employee, @PathParam("id") int id) {
		String dbURI = "mongodb://sucheta:sucheta@ds147777.mlab.com:47777/cmpe281db";
		MongoClient mongoClient = new MongoClient(new MongoClientURI(dbURI));
		DB db = mongoClient.getDB("cmpe281db");

		if (employee != null) {
			DBObject query = BasicDBObjectBuilder.start().add("_id", id).get();
			DBCollection col = db.getCollection("employee");
			DBCursor cursor = col.find(query);
			DBObject foundEmployee = null;
			if (cursor != null && cursor.hasNext()) {
				foundEmployee = cursor.next();
			}
			if (foundEmployee != null) {
				col.remove(foundEmployee);
				if (employee.getFirstName() != null) {
					foundEmployee.put("firstName", employee.getFirstName());
				}
				if (employee.getLastName() != null) {
					foundEmployee.put("lastName", employee.getLastName());
				}

				col.insert(foundEmployee);
				return Response.status(200).build();
			}
		}
		return Response.status(404).build();
	}

	@DELETE
	@Path("/{id}/")
	public Response deleteEmployee(@PathParam("id") int id) {
		DBObject query = BasicDBObjectBuilder.start().add("_id", id).get();
		String dbURI = "mongodb://sucheta:sucheta@ds147777.mlab.com:47777/cmpe281db";
		MongoClient mongoClient = new MongoClient(new MongoClientURI(dbURI));
		DB db = mongoClient.getDB("cmpe281db");
		DBCollection col = db.getCollection("employee");
		DBCursor cursor = col.find(query);
		DBObject foundEmployee = null;
		if (cursor != null && cursor.hasNext()) {
			foundEmployee = cursor.next();
		}
		if (foundEmployee != null) {
			col.remove(foundEmployee);
			return Response.status(200).build();
		} else {
			return Response.status(404).build();
		}
	}

}
