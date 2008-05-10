//package com.mkalugin.pikachu.core.tests;
//
//import static org.easymock.EasyMock.createMock;
//import static org.easymock.EasyMock.expectLastCall;
//import static org.easymock.EasyMock.notNull;
//import static org.easymock.EasyMock.replay;
//import static org.easymock.EasyMock.reset;
//import static org.easymock.EasyMock.verify;
//import junit.framework.Assert;
//
//import org.junit.Test;
//
//import com.mkalugin.pikachu.core.astxxxxx.Project;
//import com.mkalugin.pikachu.core.model.ModelConsumer;
//import com.mkalugin.pikachu.core.storage.Commit;
//import com.mkalugin.pikachu.core.storage.DataStorage;
//import com.mkalugin.pikachu.core.storage.StorageSnapshot;
//import com.mkalugin.pikachu.core.workspace.Workspace;
//import com.mkalugin.pikachu.core.workspace.WorkspaceSnapshot;
//
//public class WorkspaceTests extends Assert {
//
//	@SuppressWarnings("unchecked")
//	@Test
//	public void connectsToStorage() throws Exception {
//		DataStorage dataStorage = createMock(DataStorage.class);
//		dataStorage.registerConsumer((ModelConsumer<StorageSnapshot>) notNull());
//		replay(dataStorage);
//
//		new Workspace(dataStorage);
//		verify(dataStorage);
//	}
//
//	@Test
//	public void parsesText() throws Exception {
//		final boolean[] consumerCalled = new boolean[] { false };
//		DataStorage dataStorage = createMock(DataStorage.class);
//		StorageSnapshot snapshot = createMock(StorageSnapshot.class);
//		Workspace model = new Workspace(dataStorage);
//		model.registerConsumer(new ModelConsumer<WorkspaceSnapshot>() {
//
//			public void consume(WorkspaceSnapshot snapshot) {
//				AProject[] ast = snapshot.projects();
//				assertEquals(2, ast.length);
//				consumerCalled[0] = true;
//			}
//
//		});
//
//		snapshot.contentsOfFile("data.txt");
//		expectLastCall().andReturn("foo:\ndo\nboo:\n");
//		snapshot.timeStamp();
//		expectLastCall().andReturn(42);
//
//		replay(snapshot);
//
//		model.consume(snapshot);
//
//		verify(snapshot);
//
//		assertTrue(consumerCalled[0]);
//	}
//
//	@SuppressWarnings("unchecked")
//	@Test
//	public void pushContents() throws Exception {
//		DataStorage dataStorage = createMock(DataStorage.class);
//		ModelConsumer<WorkspaceSnapshot> consumer = createMock(ModelConsumer.class);
//		Commit commit = createMock(Commit.class);
//
//		Workspace space = new Workspace(dataStorage);
//		space.registerConsumer(consumer);
//
//		reset(dataStorage);
//
//		dataStorage.commit();
//		expectLastCall().andReturn(commit);
//		commit.add("data.txt", "hello");
//		commit.apply();
//		consumer.consume((WorkspaceSnapshot) notNull());
//		replay(dataStorage, commit, consumer);
//
//		space.pushData("hello");
//		verify(dataStorage, commit, consumer);
//	}
//
//	@SuppressWarnings("unchecked")
//	@Test
//	public void reactsToDataChange() throws Exception {
//		DataStorage dataStorage = createMock(DataStorage.class);
//		ModelConsumer<WorkspaceSnapshot> consumer = createMock(ModelConsumer.class);
//
//		Workspace workspace = new Workspace(dataStorage);
//		workspace.registerConsumer(consumer);
//
//		reset(consumer, dataStorage);
//
//		StorageSnapshot storageSnapshot = createMock(StorageSnapshot.class);
//		storageSnapshot.timeStamp();
//		expectLastCall().andReturn(42);
//		storageSnapshot.contentsOfFile("data.txt");
//		expectLastCall().andReturn("giraffe");
//
//		consumer.consume((WorkspaceSnapshot) notNull());
//
//		replay(consumer, storageSnapshot);
//
//		workspace.consume(storageSnapshot);
//
//		verify(consumer, storageSnapshot);
//	}
//
//}
