package com.mkalugin.pikachu.core.workspace;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mkalugin.pikachu.core.ast.Project;
import com.mkalugin.pikachu.core.ast.Tag;
import com.mkalugin.pikachu.core.ast.ToDoItem;

public class DocumentParser {

	private static final Pattern projectPattern = Pattern.compile("^(.+):\\s*$", Pattern.MULTILINE);
	private static final Pattern todoPattern = Pattern.compile("^\\s*-\\s*(.+)\\s*$", Pattern.MULTILINE);

	public DocumentParser() {
	}

	public Project[] parse(WorkspaceSnapshot parent, String source) {
		List<Project> result = new ArrayList<Project>();
		Matcher matcher = projectPattern.matcher(source);
		Project previousProject = null;
		while (matcher.find()) {
			int start = matcher.start(1);
			int end = matcher.end(1);
			fillProjectContents(previousProject, source, start - 1);
			Project project = new Project(parent, start, end - start + 1, 0, 0, new ToDoItem[0]);
			result.add(project);
			previousProject = project;
		}
		fillProjectContents(previousProject, source, source.length() - 1);
		return result.toArray(new Project[result.size()]);
	}

	private void fillProjectContents(Project previousProject, String source, int contentEndOffset) {
		if (previousProject != null) {
			int contentStart = previousProject.titleStart()
					+ previousProject.titleLength() + 1;
			String content = source.substring(contentStart, contentEndOffset);
			previousProject.setContentLength(content.length());
			previousProject.setTasks(parseTasks(previousProject, contentStart, content));
		}
	}

	private ToDoItem[] parseTasks(Project previousProject, int offset, String content) {
		List<ToDoItem> result = new ArrayList<ToDoItem>();
		Matcher matcher = todoPattern.matcher(content);
		while (matcher.find()) {
			int start = matcher.start(1);
			int end = matcher.end(1);

			result.add(new ToDoItem(previousProject, offset + start, end - start + 1, new Tag[0]));
		}
		return result.toArray(new ToDoItem[result.size()]);
	}

}
