package com.mkalugin.pikachu.core.tests;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.notNull;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Test;

import com.mkalugin.pikachu.core.bigmodel.StructuredModel;
import com.mkalugin.pikachu.core.bigmodel.StructuredSnapshot;
import com.mkalugin.pikachu.core.model.ModelConsumer;
import com.mkalugin.pikachu.core.userspace.UserSpace;
import com.mkalugin.pikachu.core.userspace.UserSpaceSnapshot;

public class StructuredModelTests extends Assert {
//
//	@SuppressWarnings("unchecked")
//	@Test
//	public void registersOnUserSpace() throws Exception {
//		UserSpace userSpace = createMock(UserSpace.class);
//		userSpace.registerConsumer((ModelConsumer<UserSpaceSnapshot>) notNull());
//		replay(userSpace);
//		new StructuredModel(userSpace);
//		verify(userSpace);
//	}
//
//	@Test
//	public void parsesText() throws Exception {
//		final boolean[] consumerCalled = new boolean[] { false };
//		UserSpace userSpace = createMock(UserSpace.class);
//		UserSpaceSnapshot snapshot = createMock(UserSpaceSnapshot.class);
//		StructuredModel model = new StructuredModel(userSpace);
//		model.registerConsumer(new ModelConsumer<StructuredSnapshot>() {
//
//			public void consume(StructuredSnapshot snapshot) {
//				String[] ast = snapshot.ast();
//				assertTrue(Arrays.equals(new String[] {"foo","boo"}, ast));
//			}
//			
//		});
//		
//		snapshot.content();
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
	
}

