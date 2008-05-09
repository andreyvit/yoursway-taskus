package com.mkalugin.pikachu.core.ast;

import com.mkalugin.pikachu.core.workspace.WorkspaceSnapshot;

public class Project {

	private final int titleStart;
	private final int titleLength;
	private final int contentStart;
	private final int contentLength;

	private final WorkspaceSnapshot parent;

	private final ToDoItem[] tasks;

	public Project(WorkspaceSnapshot parent, int titleStart, int titleLength, int contentStart,
			int contentLength, ToDoItem[] tasks) {
		if (parent == null)
			throw new NullPointerException("parent is null");
		if (tasks == null)
			throw new NullPointerException("tasks is null");
		
		this.contentLength = contentLength;
		this.contentStart = contentStart;
		this.parent = parent;
		this.titleLength = titleLength;
		this.titleStart = titleStart;
		this.tasks = tasks;
	}

	public WorkspaceSnapshot parent() {
		return parent;
	}

	public int titleStart() {
		return titleStart;
	}

	public int titleLength() {
		return titleLength;
	}

	public int contentStart() {
		return contentStart;
	}

	public int contentLength() {
		return contentLength;
	}

	public String title() {
		String content = parent.content();
		return content.substring(titleStart, titleStart + titleLength - 1);
	}

	public ToDoItem[] getTasks() {
		return tasks;
	}

	@Override
	public String toString() {
		return title();
	}
	
}
