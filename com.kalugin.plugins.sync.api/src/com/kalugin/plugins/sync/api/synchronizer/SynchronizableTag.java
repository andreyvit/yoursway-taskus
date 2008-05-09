package com.kalugin.plugins.sync.api.synchronizer;

public interface SynchronizableTag {
    
    String getName();
    
    boolean valueEquals(SynchronizableTag another);

    boolean nameEquals(String tagName);
    
}
