package com.mkalugin.pikachu.core.storage;

public class StorageVersion {

	private final StorageVersion parent;
	private final StorageSnapshot snapshot;

	public StorageVersion(StorageVersion parent, StorageSnapshot snapshot) {
		this.parent = parent;
		this.snapshot = snapshot;
	}

	public StorageVersion parent() {
		return parent;
	}

	public StorageSnapshot snapshot() {
		return snapshot;
	}

}
