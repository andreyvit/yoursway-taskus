package com.mkalugin.lighthouse.model;

public class Ticket {
	private final User assignedUser;
	private final Milestone milestone;
	private final int number;
	private final Project project;
	private final String state;
	private final String title;
	private final String tags;

	public Ticket(int number, String title, String tags, String state,
			User assignedUser, Project project, Milestone milestone) {
		this.assignedUser = assignedUser;
		this.milestone = milestone;
		this.number = number;
		this.project = project;
		this.state = state;
		this.tags = tags;
		this.title = title;
	}

	public User getAssignedUser() {
		return assignedUser;
	}

	public Milestone getMilestone() {
		return milestone;
	}

	public int getNumber() {
		return number;
	}

	public Project getProject() {
		return project;
	}

	public String getState() {
		return state;
	}

	public String getTitle() {
		return title;
	}

	public String getTags() {
		return tags;
	}

}
