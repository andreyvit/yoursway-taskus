package com.mkalugin.pikachu.core.storage.memory;

import org.eclipse.core.runtime.Assert;

import com.mkalugin.pikachu.core.storage.Commit;
import com.mkalugin.pikachu.core.storage.DataStorage;
import com.mkalugin.pikachu.core.storage.DataStorageBuilder;
import com.mkalugin.pikachu.core.storage.StorageException;

public class MemoryDataStorageBuilder implements DataStorageBuilder {

	public DataStorage buildFrom(String memento) {
		MemoryDataStorage storage = new MemoryDataStorage();
		Commit commit = storage.commit();
		Assert.isNotNull(commit);
		try {
			String[] lines = memento.split("\n");
			for (int i = 0; i < lines.length - 1; i += 2) {
				String filename = lines[i];
				String data = new String(Base64.decode(lines[i + 1].getBytes()));
				commit.add(filename, data);
			}
			commit.apply();		
		} catch (StorageException e) {
			commit.cancel();
		} catch (IllegalArgumentException e) {
			commit.cancel();
		}
		return storage;
	}

}
