package com.kalugin.plugins.sync.api.synchronizer;

import java.util.Collection;

public interface SynchronizableTask {
    
    String getName();
    
    TaskId getId();
    
    Collection<SynchronizableTag> tags();

    String toStringWithoutTags();

}
