package com.mkalugin.pikachu.core.tests;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.notNull;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;

import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Test;

import com.mkalugin.pikachu.core.bigmodel.StructuredModel;
import com.mkalugin.pikachu.core.bigmodel.StructuredSnapshot;
import com.mkalugin.pikachu.core.model.ModelConsumer;
import com.mkalugin.pikachu.core.storage.Commit;
import com.mkalugin.pikachu.core.storage.DataStorage;
import com.mkalugin.pikachu.core.storage.StorageSnapshot;

public class StructuredModelTests extends Assert {

	@SuppressWarnings("unchecked")
	@Test
	public void connectsToStorage() throws Exception {
		DataStorage dataStorage = createMock(DataStorage.class);
		dataStorage.registerConsumer((ModelConsumer<StorageSnapshot>) notNull());
		replay(dataStorage);

		new StructuredModel(dataStorage);
		verify(dataStorage);
	}

	@Test
	public void parsesText() throws Exception {
		final boolean[] consumerCalled = new boolean[] { false };
		DataStorage dataStorage = createMock(DataStorage.class);
		StorageSnapshot snapshot = createMock(StorageSnapshot.class);
		StructuredModel model = new StructuredModel(dataStorage);
		model.registerConsumer(new ModelConsumer<StructuredSnapshot>() {

			public void consume(StructuredSnapshot snapshot) {
				String[] ast = snapshot.ast();
				assertTrue(Arrays.equals(new String[] {"foo:", "boo:"}, ast));
				consumerCalled[0] = true;
			}

		});

		snapshot.contentsOfFile("data.txt");
		expectLastCall().andReturn("foo:\ndo\nboo:\n");
		snapshot.timeStamp();
		expectLastCall().andReturn(42);

		replay(snapshot);

		model.consume(snapshot);

		verify(snapshot);

		assertTrue(consumerCalled[0]);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void pushContents() throws Exception {
		DataStorage dataStorage = createMock(DataStorage.class);
		ModelConsumer<StructuredSnapshot> consumer = createMock(ModelConsumer.class);
		Commit commit = createMock(Commit.class);

		StructuredModel space = new StructuredModel(dataStorage);
		space.registerConsumer(consumer);

		reset(dataStorage);

		dataStorage.commit();
		expectLastCall().andReturn(commit);
		commit.add("data.txt", "hello");
		commit.apply();
		consumer.consume((StructuredSnapshot) notNull());
		replay(dataStorage, commit, consumer);

		space.pushData("hello");
		verify(dataStorage, commit, consumer);
	}

}
