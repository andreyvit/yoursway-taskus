/**
 * 
 */
package com.kalugin.plugins.sync.api.synchronizer;

public abstract class Change {
    
    protected final SynchronizableTask task;

    public Change(SynchronizableTask task) {
        if (task == null)
            throw new NullPointerException("task is null");
        this.task = task;
    }
    
    public abstract void accept(ChangeVisitor visitor);
    
}