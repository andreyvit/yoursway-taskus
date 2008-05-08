package com.mkalugin.pikachu.core.storage.file;

import java.io.File;

import com.mkalugin.pikachu.core.storage.DataStorage;
import com.mkalugin.pikachu.core.storage.DataStorageBuilder;
import com.mkalugin.pikachu.core.storage.StorageException;

public class FSDataStorageBuilder implements DataStorageBuilder {

	public DataStorage buildFrom(String memento) {
		try {
			return new FSDataStorage(new File(memento), false);
		} catch (StorageException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
}
