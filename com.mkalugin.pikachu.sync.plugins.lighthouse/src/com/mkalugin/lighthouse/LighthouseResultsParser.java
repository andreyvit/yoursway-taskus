package com.mkalugin.lighthouse;

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.mkalugin.lighthouse.model.Id.id;
import static com.mkalugin.utils.XmlUtils.filterByTag;
import static com.mkalugin.utils.XmlUtils.intValue;
import static com.mkalugin.utils.XmlUtils.intValueOrNull;
import static com.mkalugin.utils.XmlUtils.stringValue;

import java.util.Collection;

import org.w3c.dom.Element;

import com.google.common.base.Function;
import com.kalugin.plugins.sync.api.SourceQueryFailed;
import com.mkalugin.lighthouse.model.Membership;
import com.mkalugin.lighthouse.model.Milestone;
import com.mkalugin.lighthouse.model.Project;
import com.mkalugin.lighthouse.model.Ticket;
import com.mkalugin.lighthouse.model.User;
import com.mkalugin.utils.XmlUtils.XmlFormatException;

public class LighthouseResultsParser {

	private final Lighthouse lighthouse;

	public LighthouseResultsParser(Lighthouse lighthouse) {
		this.lighthouse = lighthouse;
	}
	
	public Collection<Project> parseProjects(Iterable<Element> elements) throws XmlFormatException {
		return newArrayList(transform(filterByTag(elements, "project"), PARSE_PROJECT));
	}

	public Project parseProject(Element project) {
		int id = intValue(project, "id");
		String name = stringValue(project, "name");
		return new Project(id(id), name);
	}

	public Milestone parseMilestone(Project project, Element element) {
		int id = intValue(element, "id");
		String title = stringValue(element, "title");
		return new Milestone(id(id), project, title);
	}

	public User parseUser(Element element) {
		int id = intValue(element, "id");
		String name = stringValue(element, "name");
		return new User(id(id), name);
	}

	public Collection<Ticket> parseTickets(Iterable<Element> elements) throws XmlFormatException {
		return newArrayList(transform(filterByTag(elements, "ticket"), PARSE_TICKET));
	}

	
	
	public Ticket parseTicket(Element item) {
		int number = intValue(item, "number");
		String title = stringValue(item, "title");
		String state = stringValue(item, "state");
		String tags = stringValue(item, "tag");
		Project project;
		Milestone milestone;
		User assignee;
		try {
			project = lighthouse.getProjectOrNull(id(intValueOrNull(item, "project-id")));
			milestone = lighthouse.getMilestoneOrNull(project, id(intValueOrNull(item,
			"milestone-id")));
			assignee = lighthouse.getUserOrNull(id(intValueOrNull(item, "assigned-user-id")));
		} catch (Exception e) {
			throw new SourceQueryFailed(e);
		}
		return new Ticket(number, title, tags, state, assignee, project, milestone);
	}
	
	public Membership parseMembership(Element item) {
		int id = intValue(item, "id");
		int userId = intValue(item, "user-id");
		return new Membership(id(id), id(userId));
	}

	private final Function<Element, Project> PARSE_PROJECT = new Function<Element, Project>() {

		public Project apply(Element element) {
			return parseProject(element);
		}

	};

	private final Function<Element, Ticket> PARSE_TICKET = new Function<Element, Ticket>() {

		public Ticket apply(Element element) {
			return parseTicket(element);
		}

	};

}
