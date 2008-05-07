package com.mkalugin.pikachu.core.storage.git;

import com.mkalugin.pikachu.core.model.AbstractModel;
import com.mkalugin.pikachu.core.storage.Commit;
import com.mkalugin.pikachu.core.storage.DataStorage;
import com.mkalugin.pikachu.core.storage.StorageSnapshot;
import com.mkalugin.pikachu.core.storage.StorageVersion;

public class GitDataStorage extends AbstractModel<StorageSnapshot> implements DataStorage {	
	
	public GitDataStorage() {
	}
	
	public Commit commit() {
		return null;
		
	}
	
	public StorageVersion[] log() {
		return null;
	}
	
	public void revertTo(StorageVersion version) {
		
	}

	public String getMemento() {
		return null;
	}
	
	public String getType() {
		return "com.mkalugin.pikachu.core.gitStorage";
	}
}
