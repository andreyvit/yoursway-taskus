package com.mkalugin.lighthouse.model;

public class User {
	private final Id id;
	private final String name;

	public User(Id id, String name) {
		this.id = id;
		this.name = name;
	}

	public Id getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	
	
}
