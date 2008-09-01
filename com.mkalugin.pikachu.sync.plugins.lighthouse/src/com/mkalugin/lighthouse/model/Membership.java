package com.mkalugin.lighthouse.model;

public class Membership {
	private final Id id;
	private final Id userId;

	public Membership(Id id, Id userId) {
		super();
		this.id = id;
		this.userId = userId;
	}

	public Id getId() {
		return id;
	}

	public Id getUserId() {
		return userId;
	}

}
