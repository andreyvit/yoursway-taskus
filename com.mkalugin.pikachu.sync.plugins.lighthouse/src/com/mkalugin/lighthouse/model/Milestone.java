package com.mkalugin.lighthouse.model;

public class Milestone {

	private final Id id;
	private final Project project;
	private final String title;

	public Milestone(Id id, Project project, String title) {
		this.id = id;
		this.project = project;
		this.title = title;
	}

	public Id getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}
	
	public Project getProject() {
		return project;
	}

}
