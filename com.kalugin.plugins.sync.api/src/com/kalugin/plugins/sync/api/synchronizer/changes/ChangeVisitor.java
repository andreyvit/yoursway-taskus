package com.kalugin.plugins.sync.api.synchronizer.changes;

import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTag;
import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTask;

public interface ChangeVisitor {
    
    void visitAddition(SynchronizableTask task);
    
    void visitRemoval(SynchronizableTask task);
    
    void visitRename(SynchronizableTask newerTask);

    void visitTagAddition(SynchronizableTask task, SynchronizableTag tag);
    
    void visitTagRemoval(SynchronizableTask task, SynchronizableTag tag);
    
    void visitTagValueChange(SynchronizableTask task, SynchronizableTag newerTag);
    
}
