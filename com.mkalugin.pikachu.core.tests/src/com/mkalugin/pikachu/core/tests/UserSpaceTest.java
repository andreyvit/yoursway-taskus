package com.mkalugin.pikachu.core.tests;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.notNull;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import junit.framework.Assert;

import org.junit.Test;

import com.mkalugin.pikachu.core.model.ModelConsumer;
import com.mkalugin.pikachu.core.storage.Commit;
import com.mkalugin.pikachu.core.storage.DataStorage;
import com.mkalugin.pikachu.core.storage.StorageSnapshot;
import com.mkalugin.pikachu.core.userspace.UserSpace;
import com.mkalugin.pikachu.core.userspace.UserSpaceSnapshot;

public class UserSpaceTest extends Assert {

	@Test
	public void connectsToStorage() throws Exception {
		UserSpace space = new UserSpace();
		DataStorage dataStorage = createMock(DataStorage.class);
		dataStorage.registerConsumer(space);
		replay(dataStorage);
		
		space.connectTo(dataStorage);
		verify(dataStorage);
	}
	
	@Test
	public void updatesFromStorage() throws Exception {
		final boolean[] consumerCalled = new boolean[] {false};
		UserSpace space = new UserSpace();
		space.registerConsumer(new ModelConsumer<UserSpaceSnapshot>() {

			public void consume(UserSpaceSnapshot snapshot) {
				assertNotNull(snapshot);
				assertEquals("hello", snapshot.content());
				assertEquals(42, snapshot.timeStamp());
				consumerCalled[0] = true;
			}
			
		});
		DataStorage dataStorage = createMock(DataStorage.class);
		space.connectTo(dataStorage);

		StorageSnapshot snapshot = createMock(StorageSnapshot.class);
		snapshot.contentsOfFile("data.txt");
		expectLastCall().andReturn("hello");
		snapshot.timeStamp();
		expectLastCall().andReturn(42);
		replay(snapshot);

		space.consume(snapshot);	
		verify(snapshot);
		
		assertTrue(consumerCalled[0]);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void pushContents() throws Exception {
		DataStorage dataStorage = createMock(DataStorage.class);
		ModelConsumer<UserSpaceSnapshot> consumer = createMock(ModelConsumer.class);
		Commit commit = createMock(Commit.class);
		
		UserSpace space = new UserSpace();
		space.connectTo(dataStorage);
		space.registerConsumer(consumer);
		
		reset(dataStorage);
		
		dataStorage.commit();
		expectLastCall().andReturn(commit);
		commit.add("data.txt", "hello");
		commit.apply();
		consumer.consume((UserSpaceSnapshot) notNull());
		replay(dataStorage, commit, consumer);
		
		space.pushContents("hello");
		verify(dataStorage, commit, consumer);
	}
	
}
