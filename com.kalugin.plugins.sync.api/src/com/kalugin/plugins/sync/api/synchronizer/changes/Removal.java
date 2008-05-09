/**
 * 
 */
package com.kalugin.plugins.sync.api.synchronizer.changes;

import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTask;

public class Removal extends Change {

    private SynchronizableTask task;

    public Removal(SynchronizableTask task) {
        if (task == null)
            throw new NullPointerException("task is null");
        this.task = task;
    }

    @Override
    public void accept(ChangeVisitor visitor) {
        visitor.visitRemoval(task);
    }
    
    @Override
    public String toString() {
        return "removed " + task;
    }

}