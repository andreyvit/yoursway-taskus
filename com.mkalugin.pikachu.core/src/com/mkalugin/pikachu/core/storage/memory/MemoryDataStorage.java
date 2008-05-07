package com.mkalugin.pikachu.core.storage.memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mkalugin.pikachu.core.model.AbstractModel;
import com.mkalugin.pikachu.core.model.ModelConsumer;
import com.mkalugin.pikachu.core.storage.Commit;
import com.mkalugin.pikachu.core.storage.DataStorage;
import com.mkalugin.pikachu.core.storage.StorageException;
import com.mkalugin.pikachu.core.storage.StorageSnapshot;
import com.mkalugin.pikachu.core.storage.StorageVersion;

public class MemoryDataStorage extends AbstractModel<StorageSnapshot> implements DataStorage {

	private class MemoryStorageSnapshot implements StorageSnapshot {

		private final Map<String, String> data;
		private final long time;

		public MemoryStorageSnapshot(long time, Map<String, String> data) {
			this.time = time;
			this.data = data;
		}

		public String contentsOfFile(String name) {
			return data.get(name);
		}

		public long timeStamp() {
			return time;
		}

		@Override
		public String toString() {
			StringBuilder result = new StringBuilder();
			for (String key : data.keySet()) {
				String value = data.get(key);
				result.append(key);
				result.append("\n");
				result.append(new String(Base64.encode(value.getBytes())));
				result.append("\n");
			}
			return result.toString();
		}

	}

	private class SimpleCommit implements Commit {

		private Map<String, String> data = new HashMap<String, String>();

		public void add(String path, String content) {
			data.put(path, content);
		}

		public void apply() throws StorageException {
			MemoryStorageSnapshot snapshot = new MemoryStorageSnapshot(System.currentTimeMillis(),
					data);
			StorageVersion lastVersion = (versions.isEmpty()) ? null : versions.iterator().next();
			versions.add(new StorageVersion("", lastVersion, snapshot));
			notifyConsumers(snapshot);
			currentCommit = null;
		}

		public void cancel() {
			currentCommit = null;
		}

	}

	private List<StorageVersion> versions = new ArrayList<StorageVersion>();
	private Commit currentCommit;

	public MemoryDataStorage() {
		currentCommit = null;
	}

	public Commit commit() {
		if (currentCommit != null)
			return null;
		currentCommit = new SimpleCommit();
		return currentCommit;
	}

	public StorageVersion[] log() throws StorageException {
		return versions.toArray(new StorageVersion[versions.size()]);
	}

	public void revertTo(StorageVersion version) throws StorageException {
		versions.add(version);
	}

	@Override
	public void registerConsumer(ModelConsumer<StorageSnapshot> consumer) {
		super.registerConsumer(consumer);
		if (!versions.isEmpty())
			consumer.consume(lastVersion().snapshot());
	}

	private StorageVersion lastVersion() {
		return versions.get(versions.size() - 1);
	}

	public String getMemento() {
		return lastVersion().snapshot().toString();
	}

	public String getType() {
		return "com.mkalugin.pikachu.core.memoryStorageType";
	}

	public void flush() throws StorageException {
		// nothing to do
	}

}
