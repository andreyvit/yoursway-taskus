package com.mkalugin.pikachu.core.storage;

import com.mkalugin.pikachu.core.model.Model;

public interface DataStorage extends Model<StorageSnapshot> {

	Commit commit() throws StorageException;

	StorageVersion[] log() throws StorageException;

	void revertTo(StorageVersion version) throws StorageException;

}