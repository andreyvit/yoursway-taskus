/**
 * 
 */
package com.kalugin.plugins.sync.api.synchronizer;

import com.kalugin.plugins.sync.api.synchronizer.changes.ChangeVisitor;

public final class MissingTasksVisitor implements ChangeVisitor {
    
    private final LocalizingVisitor localizer;

    public MissingTasksVisitor(LocalizingVisitor localizer) {
        this.localizer = localizer;
    }
    
    public void visitAddition(SynchronizableTask task) {
        localizer.missingTask(task);
    }
    
    public void visitRemoval(SynchronizableTask task) {
    }
    
    public void visitRename(SynchronizableTask olderTask, SynchronizableTask newerTask) {
    }
    
    public void visitTagAddition(SynchronizableTask task, SynchronizableTag tag) {
        localizer.missingTagWithLookup(task, tag);
    }
    
    public void visitTagRemoval(SynchronizableTask task, SynchronizableTag tag) {
    }
    
    public void visitTagValueChange(SynchronizableTask task, SynchronizableTag olderTag,
            SynchronizableTag newerTag) {
    }
    
    
}