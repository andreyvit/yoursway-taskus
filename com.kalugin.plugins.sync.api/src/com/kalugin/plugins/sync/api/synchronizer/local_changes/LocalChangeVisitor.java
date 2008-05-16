package com.kalugin.plugins.sync.api.synchronizer.local_changes;

import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTag;
import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTask;

public interface LocalChangeVisitor {
    
    void visitAddition(SynchronizableTask remoteTask);
    
    void visitTaskRemoval(SynchronizableTask localTask);
    
    void visitRename(SynchronizableTask olderLocalTask, SynchronizableTask newerRemoteTask);
    
    void visitIgnoredTaskAddition(SynchronizableTask remoteTask);

    void visitTagAddition(SynchronizableTask localTask, SynchronizableTag remoteTag);
    
    void visitTagRemoval(SynchronizableTask localTask, SynchronizableTag localTag);
    
    void visitTagValueChange(SynchronizableTask localTask, SynchronizableTag olderLocalTag, SynchronizableTag newerRemoteTag);
    
}
