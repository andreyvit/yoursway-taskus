package com.mkalugin.pikachu.core.ast;

public class Tag {

	private final int startOffset;
	private final int length;

	private final String name;
	private final String value;

	private final ToDoItem parent;

	public Tag(ToDoItem parent, int startOffset, int length, String name, String value) {
		this.startOffset = startOffset;
		this.length = length;
		this.parent = parent;
		this.name = name;
		this.value = value;
	}

	public int startOffset() {
		return startOffset;
	}

	public int length() {
		return length;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public ToDoItem getParent() {
		return parent;
	}
	
	@Override
	public String toString() {
		return "@" + name + "(" + value + ")";
	}

}
