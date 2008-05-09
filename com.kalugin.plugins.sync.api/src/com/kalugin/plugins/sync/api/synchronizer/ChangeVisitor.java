package com.kalugin.plugins.sync.api.synchronizer;

public interface ChangeVisitor {
    
    void visitAddition(SynchronizableTask task);
    
    void visitRemoval(SynchronizableTask task);
    
    void visitRename(SynchronizableTask newerTask);
    
}
