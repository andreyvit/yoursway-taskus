package com.mkalugin.pikachu.core.tests;

import com.mkalugin.pikachu.core.storage.DataStorage;
import com.mkalugin.pikachu.core.storage.file.FSDataStorage;
import com.mkalugin.pikachu.core.util.FileUtils;

public class FileStorageTests extends StorageConfomanceTests {
	
	@Override
	protected DataStorage createStorage() throws Exception {
		return new FSDataStorage(FileUtils.createTempFolder("filestorage", "test"));
	}
	
}
