package com.mkalugin.pikachu.core.storage;

public interface Commit {

	void add(String path, String content) throws StorageException;

	void cancel();

	void apply() throws StorageException;

}
