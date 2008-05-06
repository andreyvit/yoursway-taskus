package com.mkalugin.pikachu.core.userspace;

import com.mkalugin.pikachu.core.model.AbstractModel;
import com.mkalugin.pikachu.core.model.ModelConsumer;
import com.mkalugin.pikachu.core.storage.Commit;
import com.mkalugin.pikachu.core.storage.DataStorage;
import com.mkalugin.pikachu.core.storage.StorageException;
import com.mkalugin.pikachu.core.storage.StorageSnapshot;

public class UserSpace extends AbstractModel<UserSpaceSnapshot> implements
		ModelConsumer<StorageSnapshot> {

	private UserSpaceSnapshot lastSnapshot;
	private DataStorage storage;

	public UserSpace() {
	}

	public void connectTo(DataStorage newStorage) {
		if (newStorage == null)
			throw new NullPointerException("storage is null");

		if (this.storage != null) {
			this.storage.unregisterConsumer(this);
		}
		this.storage = newStorage;
		this.storage.registerConsumer(this);
	}

	public void consume(StorageSnapshot snapshot) {
		String newContents = snapshot.contentsOfFile("data.txt");
		if (newContents == null)
			throw new NullPointerException("content of data.txt is null");
		pushSnapshot(snapshot.timeStamp(), newContents);
	}

	private synchronized void pushSnapshot(long time, String newContents) {
		if (lastSnapshot == null || !newContents.equals(lastSnapshot.content())) {
			lastSnapshot = new UserSpaceSnapshot(time, newContents);
			notifyConsumers(lastSnapshot);
		}
	}

	public void pushContents(String contents) throws StorageException {
		if (storage == null)
			throw new IllegalStateException("storage is not set");
		Commit commit = storage.commit();
		commit.add("data.txt", contents);
		commit.apply();
		pushSnapshot(System.currentTimeMillis(), contents);
	}

}
