package com.mkalugin.pikachu.core.workspace;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mkalugin.pikachu.core.model.AbstractModel;
import com.mkalugin.pikachu.core.model.ModelConsumer;
import com.mkalugin.pikachu.core.storage.Commit;
import com.mkalugin.pikachu.core.storage.DataStorage;
import com.mkalugin.pikachu.core.storage.StorageException;
import com.mkalugin.pikachu.core.storage.StorageSnapshot;

public class Workspace extends AbstractModel<WorkspaceSnapshot> implements
		ModelConsumer<StorageSnapshot> {

	private static final String DATA_TXT = "data.txt";

	private static final Pattern pattern = Pattern.compile("^(.*):$", Pattern.MULTILINE);

	private WorkspaceSnapshot lastSnapshot;
	private DataStorage dataStorage = null;

	public Workspace() {
	}

	public Workspace(DataStorage storage) {
		connectTo(storage);
	}

	public void connectTo(DataStorage storage) {
		if (storage == null)
			throw new NullPointerException("storage is null");
		if (dataStorage != null) {
			dataStorage.unregisterConsumer(this);
		}
		this.dataStorage = storage;
		this.dataStorage.registerConsumer(this);
	}

	protected void updateTo(StorageSnapshot source) {
		String mainFile = null;
		try {
			mainFile = source.contentsOfFile(DATA_TXT);
		} catch (StorageException e) {
			// TODO
		}
		if (mainFile == null)
			mainFile = "";
		pushSnapshot(source.timeStamp(), mainFile);
	}

	protected synchronized void pushSnapshot(long time, String mainFile) {
		if (lastSnapshot == null || !mainFile.equals(lastSnapshot.content())) {
			lastSnapshot = new WorkspaceSnapshot(time, mainFile, parse(mainFile));
			notifyConsumers(lastSnapshot);
		}
	}

	protected String[] parse(String content) {
		List<String> result = new ArrayList<String>();
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			result.add(matcher.group(1));
		}
		return result.toArray(new String[result.size()]);
	}

	public void consume(StorageSnapshot snapshot) {
		updateTo(snapshot);
	}

	public void pushData(String data) throws StorageException {
		if (dataStorage == null)
			throw new IllegalStateException("No data storage specified.");
		saveToStorage(dataStorage);
		pushSnapshot(System.currentTimeMillis(), data);
	}

	public void registerConsumer(ModelConsumer<WorkspaceSnapshot> consumer) {
		super.registerConsumer(consumer);
		if (lastSnapshot != null)
			consumer.consume(lastSnapshot);
	}

	public void flush() throws StorageException {
		if (dataStorage == null)
			throw new IllegalStateException("No data storage specified.");
		dataStorage.flush();
	}

	public void synchronize() throws StorageException {
		pushData(lastSnapshot.content() + "\nLast sync: " + System.currentTimeMillis() + "\n");
	}

	public DataStorage storage() {
		return dataStorage;
	}

	public void saveToStorage(DataStorage storage) throws StorageException {
		if (storage == null)
			throw new NullPointerException("storage is null");
		Commit commit = null;
		try {
			commit = storage.commit();
			if (commit == null)
				throw new StorageException("Concurrent write attepmt.");
			commit.add(DATA_TXT, lastSnapshot.content());
			commit.apply();
		} catch (StorageException e) {
			if (commit != null)
				commit.cancel();
			throw new StorageException(e);
		} 
	}
	
}
