package com.mkalugin.pikachu.core.workspace;

import com.mkalugin.pikachu.core.storage.Commit;
import com.mkalugin.pikachu.core.storage.DataStorage;
import com.mkalugin.pikachu.core.storage.StorageException;

public class StorageWriteScheduler implements Runnable {

	private static long DELAY = 1000; // 1 sec

	private Object lock = new Object();
	private String documentState = null;

	private DataStorage storage;

	private Thread thread;

	private boolean alive;

	public void commit(String newDocumentState) {
		synchronized (lock) {
			documentState = newDocumentState;
		}
	}

	public void start(DataStorage storage) {
		flush();
		this.storage = storage;
		alive = true;
		thread = new Thread(this);
		thread.start();
	}

	public void flush() {
		alive = false;
		synchronized (lock) {			
			lock.notifyAll();
		}
		if (thread != null) {
			while (thread.isAlive()) {
				try {
					thread.join();
				} catch (InterruptedException e) {
				}
			}
		}
		if (documentState != null) {
			writeToStorage(storage, documentState);
			documentState = null;
		}
	}

	public static void writeToStorage(DataStorage dataStorage, String state) {
		if (dataStorage == null)
			throw new NullPointerException("dataStorage is null");
		Commit commit = null;
		try {
			commit = dataStorage.commit();
			if (commit == null)
				throw new StorageException("Concurrent write attepmt.");
			commit.add("data.txt", state);
			commit.apply();
		} catch (StorageException e) {
			if (commit != null)
				commit.cancel();
			throw new RuntimeException(e);
		}
	}

	public void run() {
		while (alive) {
			synchronized (lock) {
				if (documentState != null) {
					writeToStorage(storage, documentState);
					documentState = null;
				}
				try {
					lock.wait(DELAY);
				} catch (InterruptedException e) {
				}
			}
		}
	}

}
