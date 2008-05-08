package com.mkalugin.pikachu.core.storage.file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mkalugin.pikachu.core.model.AbstractModel;
import com.mkalugin.pikachu.core.model.ModelConsumer;
import com.mkalugin.pikachu.core.storage.Commit;
import com.mkalugin.pikachu.core.storage.DataStorage;
import com.mkalugin.pikachu.core.storage.StorageException;
import com.mkalugin.pikachu.core.storage.StorageSnapshot;
import com.mkalugin.pikachu.core.storage.StorageVersion;
import com.mkalugin.pikachu.core.util.FileUtils;

public class FSDataStorage extends AbstractModel<StorageSnapshot> implements DataStorage {

	private class FileCommit implements Commit {

		private List<File> files = new ArrayList<File>();
		private String suffix;

		public FileCommit() {
			suffix = ".storage" + System.currentTimeMillis();
		}

		public void add(String name, String content) throws StorageException {
			if (name == null)
				throw new NullPointerException("path is null");
			if (content == null)
				throw new NullPointerException("content is null");
			File file = new File(path, name + suffix);
			FileWriter writer = null;
			try {
				writer = new FileWriter(file);
				writer.write(content);
			} catch (IOException e) {
				throw new StorageException(e);
			} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException e) { // a bit of paranoya here
						throw new StorageException(e);
					}
				}
			}
			files.add(file);
		}

		public void apply() throws StorageException {
			try {
				for (File f : files) {
					String filePath = f.getAbsolutePath();
					String newPath = filePath.substring(0, filePath.length() - suffix.length());
					f.renameTo(new File(newPath));
				}
			} catch (Exception e) {
				throw new StorageException(e);
			}
			currentCommit = null;
		}

		public void cancel() {
			File[] allFiles = path.listFiles();
			for (File f : allFiles) {
				if (f.getName().endsWith(suffix)) {
					f.delete();
				}
			}
			currentCommit = null;
		}

	}

	private class FileStorageSnapshot implements StorageSnapshot {

		private final long time;

		public FileStorageSnapshot(long time) {
			this.time = time;
		}

		public String contentsOfFile(String name) throws StorageException {
			File file = new File(path, name);
			try {
				return FileUtils.readAsString(file);
			} catch (IOException e) {
				throw new StorageException(e);
			}
		}

		public long timeStamp() {
			return time;
		}

	}

	private final File path;
	private FileCommit currentCommit = null;
	private Object commitLock = new Object();
	private StorageVersion currentVersion;

	public FSDataStorage(File path, boolean allowToFormat) throws StorageException {
		if (path == null)
			throw new NullPointerException("path is null");
		if (!validateLocation(path)) {
			if (allowToFormat)
				formatLocation(path);
			else 
				throw new StorageException("Damaged storage or not a storage at all.");
		}		
		this.path = path;
		updateCurrentVersion();
	}

	private boolean validateLocation(File location) {
		if (!location.exists() || !location.isDirectory())
			return false;
		return new File(location, ".storage").exists();
	}

	private void formatLocation(File location) throws StorageException {
		try {
			location.mkdirs();
			new File(location, ".storage").createNewFile();
		} catch (Exception e) {
			throw new StorageException(e);
		}
	}

	public Commit commit() {
		synchronized (commitLock) {
			if (currentCommit != null)
				return null;
			currentCommit = new FileCommit();
			return currentCommit;
		}
	}

	public StorageVersion[] log() {
		return new StorageVersion[] { currentVersion };
	}

	public void revertTo(StorageVersion version) {
		throw new UnsupportedOperationException();
	}

	public String getMemento() {
		return path.toString();
	}

	public String getType() {
		return "com.mkalugin.pikachu.core.fileStorageType";
	}

	public void flush() throws StorageException {
	}
	
	public void registerConsumer(ModelConsumer<StorageSnapshot> consumer) {
		super.registerConsumer(consumer);
		consumer.consume(currentVersion.snapshot());
	}
	
	protected void updateCurrentVersion() {
		FileStorageSnapshot snapshot = new FileStorageSnapshot(System.currentTimeMillis());
		currentVersion = new StorageVersion(null, snapshot);
	}

	public String getDescription() {
		return path.getAbsolutePath();
	}
}
