/**
 * 
 */
package com.kalugin.plugins.sync.api.synchronizer;

import com.google.common.base.Predicate;
import com.kalugin.plugins.sync.api.synchronizer.changes.Change;
import com.kalugin.plugins.sync.api.synchronizer.changes.ChangeVisitor;

final class TaskRemovalCheckVisitor implements ChangeVisitor {
    
    private boolean isTaskRemoval = false;
    public static final Predicate<Change> IS_TASK_REMOVAL = new Predicate<Change>() {
    
        public boolean apply(Change change) {
            TaskRemovalCheckVisitor visitor = new TaskRemovalCheckVisitor();
            change.accept(visitor);
            return visitor.isTaskRemoval();
        }
        
    }; 
    
    public void visitAddition(SynchronizableTask task) {
    }
    
    public void visitRemoval(SynchronizableTask task) {
        isTaskRemoval = true;
    }
    
    public void visitRename(SynchronizableTask olderTask, SynchronizableTask newerTask) {
    }
    
    public void visitTagAddition(SynchronizableTask task, SynchronizableTag tag) {
    }
    
    public void visitTagRemoval(SynchronizableTask task, SynchronizableTag tag) {
    }
    
    public void visitTagValueChange(SynchronizableTask task, SynchronizableTag olderTag,
            SynchronizableTag newerTag) {
    }
    
    public boolean isTaskRemoval() {
        return isTaskRemoval;
    }
    
}