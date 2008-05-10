//package com.mkalugin.pikachu.core.tests;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import com.mkalugin.pikachu.core.model.ModelConsumer;
//import com.mkalugin.pikachu.core.storage.Commit;
//import com.mkalugin.pikachu.core.storage.DataStorage;
//import com.mkalugin.pikachu.core.storage.StorageSnapshot;
//import com.mkalugin.pikachu.core.storage.StorageVersion;
//
//import junit.framework.Assert;
//
//public abstract class StorageConfomanceTests extends Assert {
//
//	protected DataStorage storage;
//
//	protected abstract DataStorage createStorage() throws Exception;
//	
//	@Before
//	public void setUp() throws Exception {
//		storage = createStorage();
//	}
//	
//	@Test
//	public void storageIsEmpty() throws Exception {
//		assertEquals(0, storage.log().length);
//	}
//
//	@Test
//	public void commit() throws Exception {
//		Commit commit = storage.commit();
//		commit.add("foo", "bar");
//		commit.apply();
//		
//		assertEquals(1, storage.log().length);
//		
//		StorageVersion version = storage.log()[0];
//		assertEquals(null, version.parent());
//		assertEquals("bar", version.snapshot().contentsOfFile("foo"));
//	}
//
//	@Test
//	public void notifiesAboutCommit() throws Exception {
//		final StorageSnapshot[] ss = new StorageSnapshot[] {null};
//		storage.registerConsumer(new ModelConsumer<StorageSnapshot>() {
//	
//			public void consume(StorageSnapshot snapshot) {
//				ss[0] = snapshot;
//			}
//			
//		});
//		Commit commit = storage.commit();
//		commit.add("foo1", "bar1");
//		commit.apply();
//		assertNotNull(ss[0]);
//		StorageSnapshot snapshot = ss[0];
//		assertEquals("bar1", snapshot.contentsOfFile("foo1"));
//	}
//
//	@Test
//	public void cancelsCommit() throws Exception {
//		int logSize = storage.log().length;
//		Commit commit = storage.commit();
//		commit.add("foo1", "bar1");
//		commit.cancel();
//		assertEquals(logSize, storage.log().length);		
//	}
//
//	// TODO: add tests for revert()
//	
//}