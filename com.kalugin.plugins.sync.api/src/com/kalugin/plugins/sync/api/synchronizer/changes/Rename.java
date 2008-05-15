/**
 * 
 */
package com.kalugin.plugins.sync.api.synchronizer.changes;

import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTask;

public class Rename extends Change {
    
    private SynchronizableTask newerTask;
    private final SynchronizableTask olderTask;

    public Rename(SynchronizableTask olderTask, SynchronizableTask newerTask) {
        if (olderTask == null)
            throw new NullPointerException("olderTask is null");
        if (newerTask == null)
            throw new NullPointerException("task is null");
        this.olderTask = olderTask;
        this.newerTask = newerTask;
    }

    @Override
    public void accept(ChangeVisitor visitor) {
        visitor.visitRename(olderTask, newerTask);
    }
 
    @Override
    public String toString() {
        return "renamed " + newerTask;
    }

}