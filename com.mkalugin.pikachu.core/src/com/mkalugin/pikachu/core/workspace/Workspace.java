package com.mkalugin.pikachu.core.workspace;

import com.mkalugin.pikachu.core.model.AbstractModel;
import com.mkalugin.pikachu.core.model.ModelConsumer;
import com.mkalugin.pikachu.core.storage.DataStorage;
import com.mkalugin.pikachu.core.storage.StorageException;
import com.mkalugin.pikachu.core.storage.StorageSnapshot;

public class Workspace extends AbstractModel<WorkspaceSnapshot> implements
		ModelConsumer<StorageSnapshot> {

	private WorkspaceSnapshot lastSnapshot;
	private DataStorage dataStorage = null;
	private DocumentParser parser;
	private StorageWriteScheduler scheduler;

	public Workspace() {
		parser = new DocumentParser();
		scheduler = new StorageWriteScheduler();
	}

	public Workspace(DataStorage storage) {
		this();
		connectTo(storage);
	}

	public void connectTo(DataStorage storage) {
		if (storage == null)
			throw new NullPointerException("storage is null");
		if (dataStorage != null) {
			dataStorage.unregisterConsumer(this);
		}
		dataStorage = storage;
		dataStorage.registerConsumer(this);
		scheduler.start(storage);
	}

	protected void addToQueue() {
		scheduler.commit(lastSnapshot.content());
	}

	protected synchronized void addNewSnapshot(long time, String mainFile) {
		if (lastSnapshot == null || !mainFile.equals(lastSnapshot.content())) {
			lastSnapshot = new WorkspaceSnapshot(time, mainFile);
			lastSnapshot.setProjects(parser.parse(lastSnapshot, mainFile));
			notifyConsumers(lastSnapshot);
		}
	}

	public void consume(StorageSnapshot snapshot) {
		String mainFile = null;
		try {
			mainFile = snapshot.contentsOfFile("data.txt");
		} catch (StorageException e) {
		}
		if (mainFile == null)
			mainFile = "";
		addNewSnapshot(snapshot.timeStamp(), mainFile);
	}

	public void pushData(String data) {
		if (dataStorage == null)
			throw new IllegalStateException("No data storage specified.");
		addNewSnapshot(System.currentTimeMillis(), data);
		addToQueue();
	}

	public void registerConsumer(ModelConsumer<WorkspaceSnapshot> consumer) {
		super.registerConsumer(consumer);
		if (lastSnapshot != null)
			consumer.consume(lastSnapshot);
	}

	public void flush() throws StorageException {
		if (dataStorage == null)
			throw new IllegalStateException("No data storage specified.");
		addToQueue();
		scheduler.flush();
	}

	public void synchronize() throws StorageException {
		pushData(lastSnapshot.content() + "\nLast sync: " + System.currentTimeMillis() + "\n");
	}

	public DataStorage storage() {
		return dataStorage;
	}

	public synchronized void saveToStorage(DataStorage storage) throws StorageException {
		StorageWriteScheduler.writeToStorage(dataStorage, lastSnapshot.content());
	}

}
