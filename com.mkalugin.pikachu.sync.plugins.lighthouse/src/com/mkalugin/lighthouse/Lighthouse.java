package com.mkalugin.lighthouse;

import static com.google.common.collect.Maps.newHashMap;
import static com.mkalugin.utils.UrlUtils.appendPath;
import static com.mkalugin.utils.XmlUtils.childElements;
import static com.mkalugin.utils.XmlUtils.parseXmlString;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTag;
import com.mkalugin.lighthouse.model.Id;
import com.mkalugin.lighthouse.model.Membership;
import com.mkalugin.lighthouse.model.Milestone;
import com.mkalugin.lighthouse.model.Project;
import com.mkalugin.lighthouse.model.Ticket;
import com.mkalugin.lighthouse.model.User;
import com.mkalugin.utils.XmlUtils.XmlFormatException;

public class Lighthouse {

	private final URL url;
	private HttpClient client;
	private LighthouseResultsParser resultsParser;

	private Map<Id, Project> idsToProject = newHashMap();
	private Map<Id, User> idsToUser = newHashMap();

	public Lighthouse(URL url, String userName, String password) {
		if (url == null)
			throw new NullPointerException("url is null");
		if (userName == null)
			throw new NullPointerException("userName is null");
		if (password == null)
			throw new NullPointerException("password is null");
		this.url = url;
		this.client = createHttpClient(userName, password);
	}

	private HttpClient createHttpClient(String userName, String password) {
		HttpClient client = new HttpClient();
		client.getParams().setAuthenticationPreemptive(true);
		UsernamePasswordCredentials cred = new UsernamePasswordCredentials(userName, password);
		client.getState().setCredentials(
				new AuthScope(url.getHost(), AuthScope.ANY_PORT, AuthScope.ANY_REALM), cred);
		return client;
	}

	public Collection<Project> listProjects() throws LighthouseException {
		try {
			return createParser().parseProjects(childElements(doGET("/projects.xml")));
		} catch (XmlFormatException e) {
			throw new LighthouseException(e);
		} catch (IOException e) {
			throw new LighthouseException(e);
		} catch (ParserConfigurationException e) {
			throw new LighthouseException(e);
		} catch (SAXException e) {
			throw new LighthouseException(e);
		}
	}

	Project getProjectOrNull(Id id) throws HttpException, LighthouseAuthenticationException,
			IOException, ParserConfigurationException, SAXException {
		if (id == null)
			return null;
		Project project = idsToProject.get(id);
		if (project == null) {
			project = createParser().parseProject(doGET("/projects/" + id + ".xml"));
			idsToProject.put(id, project);
		}
		return project;
	}

	Milestone getMilestoneOrNull(Project project, Id id) throws HttpException,
			LighthouseAuthenticationException, IOException, ParserConfigurationException,
			SAXException {
		if (id == null)
			return null;
		return createParser().parseMilestone(project,
				doGET("/projects/" + project.getId() + "/milestones/" + id + ".xml"));
	}

	User getUserOrNull(Id id) throws HttpException, LighthouseAuthenticationException, IOException,
			ParserConfigurationException, SAXException {
		if (id == null)
			return null;
		User user = idsToUser.get(id);
		if (user == null) {
			user = createParser().parseUser(doGET("/users/" + id + ".xml"));
			idsToUser.put(id, user);
		}
		return user;
	}

	public Collection<Ticket> readTickets(Project project, String query) throws LighthouseException {
		try {
			return createParser()
					.parseTickets(
							childElements(doGET("/projects/" + project.getId() + "/tickets.xml?q="
									+ query))); // todo escape query
		} catch (XmlFormatException e) {
			throw new LighthouseException(e);
		} catch (IOException e) {
			throw new LighthouseException(e);
		} catch (ParserConfigurationException e) {
			throw new LighthouseException(e);
		} catch (SAXException e) {
			throw new LighthouseException(e);
		}
	}

	public void complete(Ticket ticket) throws LighthouseException {
		try {
			doPUT("/projects/" + ticket.getProject().getId() + "/tickets/" + ticket.getNumber()
					+ ".xml", "<ticket><state>resolved</state></ticket>");
		} catch (IOException e) {
			throw new LighthouseException(e);
		} catch (ParserConfigurationException e) {
			throw new LighthouseException(e);
		} catch (SAXException e) {
			throw new LighthouseException(e);
		}
	}

	public void uncomplete(Ticket ticket) throws LighthouseException {
		try {
			doPUT("/projects/" + ticket.getProject().getId() + "/tickets/" + ticket.getNumber()
					+ ".xml", "<ticket><state>open</state></ticket>");
		} catch (IOException e) {
			throw new LighthouseException(e);
		} catch (ParserConfigurationException e) {
			throw new LighthouseException(e);
		} catch (SAXException e) {
			throw new LighthouseException(e);
		}
	}

	public void deleteItem(Ticket ticket) throws LighthouseException {
		try {
			doDELETE("/projects/" + ticket.getProject().getId() + "/tickets/" + ticket.getNumber()
					+ ".xml");
		} catch (IOException e) {
			throw new LighthouseException(e);
		} catch (ParserConfigurationException e) {
			throw new LighthouseException(e);
		} catch (SAXException e) {
			throw new LighthouseException(e);
		}
	}

	public Ticket rename(Ticket ticket, String newTitle) throws LighthouseException {
		try {
			String ticketPath = "/projects/" + ticket.getProject().getId() + "/tickets/"
					+ ticket.getNumber() + ".xml";
			doPUT(ticketPath, "<ticket><title>" + newTitle + "</title></ticket>");
			return createParser().parseTicket(doGET(ticketPath));
		} catch (IOException e) {
			throw new LighthouseException(e);
		} catch (ParserConfigurationException e) {
			throw new LighthouseException(e);
		} catch (SAXException e) {
			throw new LighthouseException(e);
		}
	}

	String tagToXML(Project project, String name, String value) throws HttpException,
			LighthouseAuthenticationException, IOException, ParserConfigurationException,
			SAXException {
		if (name.equals("milestone")) {
			Id mid = findMilestoneId(project, value);
			if (mid != null)
				return "<milestone-id type=\"integer\">" + mid + "</milestone-id>";
		} else if (name.equals("assignee")) {
			Id aid = findUserId(project, value);
			if (aid != null)
				return "<assigened-user-id type=\"integer\">" + aid + "</assigned-user-id>";
		} else if (name.equals("state")) {
			return "<state>" + value + "</state>";
		}
		return null;
	}

	public void createTicket(Project project, String title, Collection<SynchronizableTag> tags)
			throws LighthouseException {
		try {
			StringBuilder ticket = new StringBuilder();
			for (SynchronizableTag tag : tags) {
				String xml = tagToXML(project, tag.getName(), tag.getValue());
				if (xml != null)
					ticket.append(xml);
			}
			ticket.append("<title>" + title + "</title>");
			doPOST("/projects/" + project.getId() + "/tickets.xml", "<ticket>" + ticket.toString()
					+ "</ticket>");
		} catch (IOException e) {
			throw new LighthouseException(e);
		} catch (ParserConfigurationException e) {
			throw new LighthouseException(e);
		} catch (SAXException e) {
			throw new LighthouseException(e);
		}
	}

	Id findUserId(Project project, String value) throws HttpException,
			LighthouseAuthenticationException, IOException, ParserConfigurationException,
			SAXException {
		LighthouseResultsParser parser = createParser();
		Element membershipsElement = doGET("/projects/" + project.getId() + "/memberships.xml");
		for (Element el : childElements(membershipsElement)) {
			Membership membership = parser.parseMembership(el);
			User user = getUserOrNull(membership.getUserId());
			if (user != null && user.getName().equals(value))
				return user.getId();
		}
		return null;
	}

	Id findMilestoneId(Project project, String value) throws HttpException,
			LighthouseAuthenticationException, IOException, ParserConfigurationException,
			SAXException {
		LighthouseResultsParser parser = createParser();
		Element milestonesElement = doGET("/projects/" + project.getId() + "/milestones.xml");
		for (Element el : childElements(milestonesElement)) {
			Milestone milestone = parser.parseMilestone(project, el);
			if (milestone.getTitle().equals(value))
				return milestone.getId();
		}
		return null;
	}

	private void doPUT(String relativePath, String request) throws HttpException, IOException,
			ParserConfigurationException, SAXException, LighthouseAuthenticationException {
		PutMethod httpMethod = new PutMethod(appendPath(url, relativePath).toExternalForm());
		httpMethod.setRequestHeader("Accept", "application/xml");
		StringRequestEntity entity = new StringRequestEntity(request, "application/xml", "utf-8");
		httpMethod.setRequestEntity(entity);

		executeAndCheckResponse(httpMethod, relativePath);
	}

	private void doPOST(String relativePath, String request) throws HttpException, IOException,
			ParserConfigurationException, SAXException, LighthouseAuthenticationException {
		PostMethod httpMethod = new PostMethod(appendPath(url, relativePath).toExternalForm());
		httpMethod.setRequestHeader("Accept", "application/xml");
		StringRequestEntity entity = new StringRequestEntity(request, "application/xml", "utf-8");
		httpMethod.setRequestEntity(entity);

		executeAndCheckResponse(httpMethod, relativePath);
	}

	private Element doGET(String relativePath) throws HttpException, IOException,
			ParserConfigurationException, SAXException, LighthouseAuthenticationException {
		GetMethod httpMethod = new GetMethod(appendPath(url, relativePath).toExternalForm());

		String resp = executeAndCheckResponse(httpMethod, relativePath);
		return parseXmlString(resp).getDocumentElement();
	}

	private void doDELETE(String relativePath) throws HttpException, IOException,
			ParserConfigurationException, SAXException, LighthouseAuthenticationException {
		DeleteMethod httpMethod = new DeleteMethod(appendPath(url, relativePath).toExternalForm());

		executeAndCheckResponse(httpMethod, relativePath);
	}

	private String executeAndCheckResponse(HttpMethod httpMethod, String relativePath)
			throws HttpException, IOException, ParserConfigurationException, SAXException,
			LighthouseAuthenticationException {
		int result = client.executeMethod(httpMethod);
		String resp = httpMethod.getResponseBodyAsString();
		if (result == 401)
			throw new LighthouseAuthenticationException();
		if (result != 200 && result != 201)
			throw new IOException("Failed to communicate with Lighthouse: result is " + result
					+ "(url was " + relativePath + ")\n" + resp);
		System.out.println(resp);
		return resp;
	}

	private LighthouseResultsParser createParser() {
		if (resultsParser == null)
			resultsParser = new LighthouseResultsParser(this);
		return resultsParser;
	}

	private void saveTicketWithXML(Ticket item, String xml) throws LighthouseException {
		try {
			Project project = item.getProject();
			doPUT("/projects/" + project.getId() + "/tickets/" + item.getNumber() + ".xml",
					"<ticket>" + xml + "</ticket>");
		} catch (IOException e) {
			throw new LighthouseException(e);
		} catch (ParserConfigurationException e) {
			throw new LighthouseException(e);
		} catch (SAXException e) {
			throw new LighthouseException(e);
		}
	}

	public void addTag(Ticket item, String name, String value) throws LighthouseException {
		try {
			String xml = tagToXML(item.getProject(), name, value);
			if (xml == null) {
				xml = "<tag>" + item.getTags() + " " + name + "</tag>";
			}
			saveTicketWithXML(item, xml);
		} catch (IOException e) {
			throw new LighthouseException(e);
		} catch (ParserConfigurationException e) {
			throw new LighthouseException(e);
		} catch (SAXException e) {
			throw new LighthouseException(e);
		}
	}
	

	public void removeTag(Ticket item, String name) throws LighthouseException {
		StringBuilder result = new StringBuilder();
		String tags = item.getTags();
		String[] split = tags.split(" ");
		for (String s : split) {
			if (!s.equals(name) && s.length() > 0) {
				result.append(s + " ");
			}
		}
		String xml = "<tag>" + result.toString().trim() + "</tag>";
		saveTicketWithXML(item, xml);
	}

	public void updateTagValue(Ticket item, String name, String value) throws LighthouseException {
		try {
			String xml = tagToXML(item.getProject(), name, value);
			if (xml != null) {
				saveTicketWithXML(item, xml);
			}
		} catch (IOException e) {
			throw new LighthouseException(e);
		} catch (ParserConfigurationException e) {
			throw new LighthouseException(e);
		} catch (SAXException e) {
			throw new LighthouseException(e);
		}
	}

}
