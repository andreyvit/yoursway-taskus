package com.mkalugin.pikachu.core.storage;

public class StorageVersion {

	private final StorageVersion parent;
	private final String description;
	private final StorageSnapshot snapshot;

	public StorageVersion(String description, StorageVersion parent, StorageSnapshot snapshot) {
		this.description = description;
		this.parent = parent;
		this.snapshot = snapshot;
	}

	public StorageVersion parent() {
		return parent;
	}

	public String description() {
		return description;
	}

	public StorageSnapshot snapshot() {
		return snapshot;
	}

}
