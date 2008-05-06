package com.mkalugin.pikachu.core.tests;

import junit.framework.Assert;

import org.junit.Test;

import com.mkalugin.pikachu.core.model.ModelConsumer;
import com.mkalugin.pikachu.core.storage.Commit;
import com.mkalugin.pikachu.core.storage.StorageSnapshot;
import com.mkalugin.pikachu.core.storage.StorageVersion;
import com.mkalugin.pikachu.core.storage.memory.MemoryDataStorage;

public class MemoryStorageTests extends Assert {

	@Test
	public void clearMemoryStorage() throws Exception {
		MemoryDataStorage storage = new MemoryDataStorage();
		assertEquals(0, storage.log().length);
	}
	
	@Test
	public void commitToMemoryStorage() throws Exception {
		MemoryDataStorage storage = new MemoryDataStorage();
		assertEquals(0, storage.log().length);
		Commit commit = storage.commit();
		commit.add("foo", "bar");
		commit.apply();
		assertEquals(1, storage.log().length);
		StorageVersion version = storage.log()[0];
		assertEquals(null, version.parent());
		assertEquals("bar", version.snapshot().contentsOfFile("foo"));
	}
	
	@Test
	public void notifiesAboutCommit() throws Exception {
		final StorageSnapshot[] ss = new StorageSnapshot[] {null};
		MemoryDataStorage storage = new MemoryDataStorage();
		storage.registerConsumer(new ModelConsumer<StorageSnapshot>() {

			public void consume(StorageSnapshot snapshot) {
				ss[0] = snapshot;
			}
			
		});
		Commit commit = storage.commit();
		commit.add("foo1", "bar1");
		commit.apply();
		assertNotNull(ss[0]);
		StorageSnapshot snapshot = ss[0];
		assertEquals("bar1", snapshot.contentsOfFile("foo1"));
	}
	
	@Test
	public void cancelCommit() throws Exception {
		MemoryDataStorage storage = new MemoryDataStorage();
		assertEquals(0, storage.log().length);
		Commit commit = storage.commit();
		commit.add("foo1", "bar1");
		commit.cancel();
		assertEquals(0, storage.log().length);		
	}
	
	
}
