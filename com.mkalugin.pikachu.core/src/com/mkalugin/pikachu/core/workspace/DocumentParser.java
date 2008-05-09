package com.mkalugin.pikachu.core.workspace;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mkalugin.pikachu.core.ast.Project;
import com.mkalugin.pikachu.core.ast.ToDoItem;

public class DocumentParser {

	private static final Pattern pattern = Pattern.compile("^(.*):$", Pattern.MULTILINE);

	public DocumentParser() {
	}

	public Project[] parse(WorkspaceSnapshot parent, String source) {
		List<Project> result = new ArrayList<Project>();
		Matcher matcher = pattern.matcher(source);
		while (matcher.find()) {
			int start = matcher.start(1);
			int end = matcher.end(1);
			result.add(new Project(parent, start, end - start + 1, 0, 0, new ToDoItem[0]));
		}
		return result.toArray(new Project[result.size()]);
	}

}
