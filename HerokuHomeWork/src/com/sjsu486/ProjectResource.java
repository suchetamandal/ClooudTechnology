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

@Path("/project")
public class ProjectResource {

	@GET
	@Produces("application/xml")
	public List<ProjectConverter> getProjects() {
		List<ProjectConverter> Projects = new ArrayList<ProjectConverter>();
		String dbURI = "mongodb://sucheta:sucheta@ds147777.mlab.com:47777/cmpe281db";
		MongoClient mongoClient = new MongoClient(new MongoClientURI(dbURI));
		DB db = mongoClient.getDB("cmpe281db");

		DBCollection empCollection = db.getCollection("Project");
		DBCursor cursor = empCollection.find();
		while (cursor.hasNext()) {
			DBObject prj = cursor.next();
			Project newPrj = new Project();
			newPrj.setName((String) prj.get("name"));
			newPrj.setBudget((float) (double)prj.get("budget"));
			newPrj.setId((int) prj.get("_id"));
			ProjectConverter ProjectConverter = new ProjectConverter(newPrj);
			Projects.add(ProjectConverter);
		}
		return Projects;
	}

	@GET
	@Produces("application/json")
	@Path("/json/")
	public Response getJSONProjects() throws JSONException {
		String dbURI = "mongodb://sucheta:sucheta@ds147777.mlab.com:47777/cmpe281db";
		MongoClient mongoClient = new MongoClient(new MongoClientURI(dbURI));
		DB db = mongoClient.getDB("cmpe281db");

		DBCollection empCollection = db.getCollection("Project");
		DBCursor cursor = empCollection.find();
		List<JSONObject> Projects = new ArrayList<JSONObject>();

		while (cursor.hasNext()) {
			DBObject prj = cursor.next();
			JSONObject newPrj = new JSONObject();
			newPrj.put("name", (String) prj.get("name"));
			newPrj.put("budget", (float) (double)prj.get("budget"));
			newPrj.put("id", (int) prj.get("_id"));
			Projects.add(newPrj);
		}
		return Response.status(200).entity(Projects.toString()).build();
	}

	@GET
	@Produces("application/xml")
	@Path("/{id}/")
	public Response getProject(@PathParam("id") int id) {
		DBObject query = BasicDBObjectBuilder.start().add("_id", id).get();
		String dbURI = "mongodb://sucheta:sucheta@ds147777.mlab.com:47777/cmpe281db";
		MongoClient mongoClient = new MongoClient(new MongoClientURI(dbURI));
		DB db = mongoClient.getDB("cmpe281db");
		DBCollection col = db.getCollection("Project");
		DBCursor cursor = col.find(query);
		DBObject foundProject = null;
		if (cursor != null && cursor.hasNext()) {
			foundProject = cursor.next();
		}

		if (foundProject != null) {
			Project newPrj = new Project();
			newPrj.setName((String) foundProject.get("name"));
			newPrj.setBudget((float)(double)foundProject.get("budget"));
			newPrj.setId((int) foundProject.get("_id"));
			ProjectConverter ProjectConverter = new ProjectConverter(newPrj);
			return Response.status(200).entity(ProjectConverter).build();
		} else {
			return Response.status(404).build();
		}
	}

	@GET
	@Produces("application/json")
	@Path("/json/{id}/")
	public Response getJSONProject(@PathParam("id") int id) throws JSONException {
		DBObject query = BasicDBObjectBuilder.start().add("_id", id).get();
		String dbURI = "mongodb://sucheta:sucheta@ds147777.mlab.com:47777/cmpe281db";
		MongoClient mongoClient = new MongoClient(new MongoClientURI(dbURI));
		DB db = mongoClient.getDB("cmpe281db");
		DBCollection col = db.getCollection("Project");
		DBCursor cursor = col.find(query);
		DBObject foundProject = null;
		if (cursor != null) {
			foundProject = cursor.next();
		}

		if (foundProject != null && cursor.hasNext()) {
			JSONObject newPrj = new JSONObject();
			newPrj.put("name", (String) foundProject.get("name"));
			newPrj.put("budget", (float)(double)foundProject.get("budget"));
			newPrj.put("id", (int) foundProject.get("_id"));
			return Response.status(200).entity(newPrj.toString()).build();
		} else {
			return Response.status(404).build();
		}
	}

	@POST
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response addProject(Project project) throws UnknownHostException {
		String dbURI = "mongodb://sucheta:sucheta@ds147777.mlab.com:47777/cmpe281db";
		MongoClient mongoClient = new MongoClient(new MongoClientURI(dbURI));
		DB db = mongoClient.getDB("cmpe281db");
		DBObject query = BasicDBObjectBuilder.start().add("_id", project.getId()).get();
		DBCollection collection = db.getCollection("Project");
		DBCursor cursor = collection.find(query);
		DBObject foundProject = null;
		if (cursor != null && cursor.hasNext()) {
			foundProject = cursor.next();
		}
		if (foundProject != null) {
			mongoClient.close();
			return Response.status(409).build();
		}
		DBObject dbProject = createDBObject(project);

		collection.insert(dbProject);
		String location = "http://localhost:8888/rest/Project/" + project.getId();
		mongoClient.close();
		return Response.status(201).header("location", location).build();
	}

	private static DBObject createDBObject(Project Project) {
		BasicDBObjectBuilder docBuilder = BasicDBObjectBuilder.start();

		docBuilder.append("_id", Project.getId());
		docBuilder.append("name", Project.getName());
		docBuilder.append("budget", Project.getBudget());
		return docBuilder.get();
	}

	@PUT
	@Consumes({ "application/xml", "application/json" })
	@Path("/{id}/")
	public Response updateProject(Project project, @PathParam("id") int id) {
		String dbURI = "mongodb://sucheta:sucheta@ds147777.mlab.com:47777/cmpe281db";
		MongoClient mongoClient = new MongoClient(new MongoClientURI(dbURI));
		DB db = mongoClient.getDB("cmpe281db");

		if (project != null) {
			DBObject query = BasicDBObjectBuilder.start().add("_id", id).get();
			DBCollection col = db.getCollection("Project");
			DBCursor cursor = col.find(query);
			DBObject foundProject = null;
			if (cursor != null && cursor.hasNext()) {
				foundProject = cursor.next();
			}
			if (foundProject != null) {
				col.remove(foundProject);
				if (project.getName() != null) {
					foundProject.put("name", project.getName());
				}

				col.insert(foundProject);
				return Response.status(200).build();
			}
		}
		return Response.status(404).build();
	}

	@DELETE
	@Path("/{id}/")
	public Response deleteProject(@PathParam("id") int id) {
		DBObject query = BasicDBObjectBuilder.start().add("_id", id).get();
		String dbURI = "mongodb://sucheta:sucheta@ds147777.mlab.com:47777/cmpe281db";
		MongoClient mongoClient = new MongoClient(new MongoClientURI(dbURI));
		DB db = mongoClient.getDB("cmpe281db");
		DBCollection col = db.getCollection("Project");
		DBCursor cursor = col.find(query);
		DBObject foundProject = null;
		if (cursor != null && cursor.hasNext()) {
			foundProject = cursor.next();
		}
		if (foundProject != null) {
			col.remove(foundProject);
			return Response.status(200).build();
		} else {
			return Response.status(404).build();
		}
	}

}
