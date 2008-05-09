package com.mkalugin.pikachu.core.ast;

import com.mkalugin.pikachu.core.workspace.WorkspaceSnapshot;

public class ToDoItem {

	private final int startOffset;
	private final int length;

	private final Project parent;

	private final Tag[] tags;

	public ToDoItem(Project parent, int startOffset, int length, Tag[] tags) {
		if (parent == null)
			throw new NullPointerException("parent is null");
		if (tags == null)
			throw new NullPointerException("tags is null");
		this.length = length;
		this.parent = parent;
		this.startOffset = startOffset;
		this.tags = tags;
	}

	public int startOffset() {
		return startOffset;
	}

	public int length() {
		return length;
	}

	public Project getParent() {
		return parent;
	}

	public Tag[] getTags() {
		return tags;
	}

	public String title() {
		WorkspaceSnapshot workspaceSnapshot = parent.parent();
		return workspaceSnapshot.content().substring(startOffset, startOffset + length - 1);
	}

	@Override
	public String toString() {
		return title();
	}

}
