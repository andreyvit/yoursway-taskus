package com.kalugin.plugins.sync.api.tests.synchronizer;

import org.junit.Test;

public class Tasks extends AbstractSynchronizerTest {
    
    @Test
    public void initiallyGetTasksFromRemote() throws Exception {
        go();
    }
    
    @Test
    public void locallyAdded() throws Exception {
        go();
    }
    
    // local removal does NOT remove remote tasks
    @Test
    public void locallyRemoved() throws Exception {
        go();
    }
    
    // tagging a task with @remove removes it from the remote side
    @Test
    public void locallyTaggedWithRemove() throws Exception {
        go();
    }
    
    @Test
    public void locallyRenamed() throws Exception {
        go();
    }
    
    @Test
    public void remotelyRemoved() throws Exception {
        go();
    }
    
    @Test
    public void remotelyRenamed() throws Exception {
        go();
    }
    
}
