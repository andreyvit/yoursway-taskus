package com.kalugin.plugins.sync.api.synchronizer;

public interface SynchronizableTag {
    
    String getName();
    
    String getValue();
    
    boolean valueEquals(SynchronizableTag another);

    boolean nameEquals(String tagName);
    
}
