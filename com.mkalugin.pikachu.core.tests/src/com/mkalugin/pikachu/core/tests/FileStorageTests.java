package com.mkalugin.pikachu.core.tests;

import org.junit.Test;

import com.mkalugin.pikachu.core.storage.DataStorage;
import com.mkalugin.pikachu.core.storage.file.FSDataStorage;
import com.mkalugin.pikachu.core.util.FileUtils;

public class FileStorageTests extends StorageConfomanceTests {
	
	@Override
	protected DataStorage createStorage() throws Exception {
		return new FSDataStorage(FileUtils.createTempFolder("filestorage", "test"));
	}
	
	@Test
	@Override
	public void storageIsEmpty() throws Exception {
		assertEquals(1, storage.log().length);
	}
	
}
