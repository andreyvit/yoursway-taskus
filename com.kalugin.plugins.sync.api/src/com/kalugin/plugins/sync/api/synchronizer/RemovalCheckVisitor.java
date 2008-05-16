/**
 * 
 */
package com.kalugin.plugins.sync.api.synchronizer;

import com.google.common.base.Predicate;
import com.kalugin.plugins.sync.api.synchronizer.changes.Change;
import com.kalugin.plugins.sync.api.synchronizer.changes.ChangeVisitor;

final class RemovalCheckVisitor implements ChangeVisitor {
    
    private boolean isRemoval = false;
    public static final Predicate<Change> IS_REMOVAL = new Predicate<Change>() {
    
        public boolean apply(Change change) {
            RemovalCheckVisitor visitor = new RemovalCheckVisitor();
            change.accept(visitor);
            return visitor.isRemoval();
        }
        
    }; 
    
    public void visitAddition(SynchronizableTask task) {
    }
    
    public void visitRemoval(SynchronizableTask task) {
        isRemoval = true;
    }
    
    public void visitRename(SynchronizableTask olderTask, SynchronizableTask newerTask) {
    }
    
    public void visitTagAddition(SynchronizableTask task, SynchronizableTag tag) {
    }
    
    public void visitTagRemoval(SynchronizableTask task, SynchronizableTag tag) {
        isRemoval = true;
    }
    
    public void visitTagValueChange(SynchronizableTask task, SynchronizableTag olderTag,
            SynchronizableTag newerTag) {
    }
    
    public boolean isRemoval() {
        return isRemoval;
    }
    
}