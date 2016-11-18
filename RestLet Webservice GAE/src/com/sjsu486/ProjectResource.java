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

@Path("/project")
public class ProjectResource {
	
	private static final Logger log = Logger.getLogger(ProjectResource.class.getName());
	
	@GET
	@Produces("application/xml")
	public Response getProjects() {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		List<ProjectConverter> projects = new ArrayList<ProjectConverter>();

		Query pq = new Query("Project");
		PreparedQuery preparedQ = datastore.prepare(pq);

		for (Entity prj : preparedQ.asIterable()) {
			Project newPrj = new Project();
			newPrj.setName((String) prj.getProperty("name"));
			newPrj.setBudget((float) (double) prj.getProperty("budget"));
			newPrj.setId((int) (long) prj.getProperty("id"));
			ProjectConverter projectConverter = new ProjectConverter(newPrj);
			projects.add(projectConverter);
		}
		if (projects.size() > 0) {
			XStream xstream = new XStream();
			xstream.alias("project", ProjectConverter.class);
			xstream.addImplicitCollection(ProjectList.class, "list");

			ProjectList list = new ProjectList();
			for (ProjectConverter ec : projects) {
				list.add(ec);
			}

			String xml = xstream.toXML(list);
			log.info("Found Projects");
			return Response.status(200).entity(xml).build();
		}
		return Response.status(404).build();
	}

	@GET
	@Produces("application/json")
	@Path("/json/")
	public Response getJSONProjects() throws JSONException {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		ArrayList<JSONObject> projects = new ArrayList<JSONObject>();

		Query pq = new Query("Project");
		PreparedQuery preparedQ = datastore.prepare(pq);

		for (Entity prj : preparedQ.asIterable()) {
			JSONObject newPrj = new JSONObject();
			newPrj.put("name", (String) prj.getProperty("name"));
			newPrj.put("budget", (float) (double) prj.getProperty("budget"));
			newPrj.put("id", (int) (long) prj.getProperty("id"));
			projects.add(newPrj);
		}
		if (projects.size() > 0) {
			log.info("Found Projects");
			return Response.status(200).entity(projects.toString()).build();
		}
		return Response.status(404).build();
	}

	@GET
	@Produces("application/xml")
	@Path("/{id}/")
	public Response getProject(@PathParam("id") int id) {
		Entity foundproject = findProject(id);

		if (foundproject != null) {
			Project newPrj = new Project();
			newPrj.setName((String) foundproject.getProperty("name"));
			newPrj.setBudget((float) (double) foundproject.getProperty("budget"));
			newPrj.setId((int) (long) foundproject.getProperty("id"));
			ProjectConverter projectConverter = new ProjectConverter(newPrj);
			log.info("Found Projects");
			return Response.status(200).entity(projectConverter).build();
		} else {
			log.info("No Projects Found");
			return Response.status(404).build();
		}

	}

	@GET
	@Produces("application/json")
	@Path("/json/{id}/")
	public Response getJSONProject(@PathParam("id") int id) throws JSONException {
		Entity foundproject = findProject(id);

		if (foundproject != null) {
			JSONObject newPrj = new JSONObject();
			newPrj.put("name", (String) foundproject.getProperty("name"));
			newPrj.put("budget", (float) (double) foundproject.getProperty("budget"));
			newPrj.put("id", (int) (long) foundproject.getProperty("id"));
			log.info("Found Project");
			return Response.status(200).entity(newPrj.toString()).build();
		} else {
			log.info("No Project Found");
			return Response.status(404).build();
		}
	}

	@POST
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response addProject(Project Project) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Entity newProject = new Entity("Project");
		Entity oldProject = findProject(Project.getId());
		if (oldProject != null) {
			log.info("Already Exists");
			return Response.status(409).build();
		}
		newProject.setProperty("name", Project.getName());
		newProject.setProperty("budget", Project.getBudget());
		newProject.setProperty("id", Project.getId());

		datastore.put(newProject);
		String location = "http:2-dot-cmpe281suchetamandalhw486.appspot.com/cmpe281SuchetaMandal486/rest/Project/"
				+ Project.getId();
		String jsonlocation = "http:2-dot-cmpe281suchetamandalhw486.appspot.com/cmpe281SuchetaMandal486/rest/Project/json/"
				+ Project.getId();
		log.info("One Project Created");
		return Response.status(201).header("location", location).header("jsonlocation", jsonlocation).build();
	}

	@PUT
	@Consumes({ "application/xml", "application/json" })
	@Path("/{id}/")
	public Response updateProject(Project project, @PathParam("id") int id) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		if (project != null) {
			Entity foundPrj = findProject(id);
			if (foundPrj != null) {
				if (project.getName() != null) {
					foundPrj.setProperty("name", project.getName());
				}
				if (project.getBudget() != 0) {
					foundPrj.setProperty("budget", project.getBudget());
				}
				datastore.put(foundPrj);
				log.info("Project is updated");
				return Response.status(200).build();
			}
		}
		log.info("No Project found");
		return Response.status(404).build();
	}

	@DELETE
	@Path("/{id}/")
	public Response deleteProject(@PathParam("id") int id) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Entity foundProject = findProject(id);
		if (foundProject != null) {
			datastore.delete(foundProject.getKey());
			log.info("One Project is deleted");
			return Response.status(200).build();
		} else {
			log.info("No Project found");
			return Response.status(404).build();
		}
	}

	private Entity findProject(int id) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Filter idFilter = new FilterPredicate("id", FilterOperator.EQUAL, id);
		Query findPrj = new Query("Project").setFilter(idFilter);

		PreparedQuery pq = datastore.prepare(findPrj);
		for (Entity prj : pq.asIterable()) {
			return prj;
		}
		return null;
	}

}
