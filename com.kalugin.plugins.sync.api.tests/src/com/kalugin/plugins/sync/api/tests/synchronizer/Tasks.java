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
    
    @Test
    public void locallyRemoved() throws Exception {
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
