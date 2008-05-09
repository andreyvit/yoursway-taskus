/**
 * 
 */
package com.kalugin.plugins.sync.api.synchronizer.changes;

import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTask;

public class Addition extends Change {
    
    private final SynchronizableTask task;
    
    public Addition(SynchronizableTask task) {
        if (task == null)
            throw new NullPointerException("task is null");
        this.task = task;
    }

    @Override
    public void accept(ChangeVisitor visitor) {
        visitor.visitAddition(task);
    }
    
    @Override
    public String toString() {
        return "added " + task;
    }
    
}