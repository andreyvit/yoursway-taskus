/**
 * 
 */
package com.kalugin.plugins.sync.api.synchronizer.changes;

import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTask;

public class Rename extends Change {
    
    private SynchronizableTask newerTask;

    public Rename(SynchronizableTask newerTask) {
        if (newerTask == null)
            throw new NullPointerException("task is null");
        this.newerTask = newerTask;
    }

    @Override
    public void accept(ChangeVisitor visitor) {
        visitor.visitRename(newerTask);
    }
 
    @Override
    public String toString() {
        return "renamed " + newerTask;
    }

}