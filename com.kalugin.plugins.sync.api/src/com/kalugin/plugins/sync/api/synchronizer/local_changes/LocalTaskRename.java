/**
 * 
 */
package com.kalugin.plugins.sync.api.synchronizer.local_changes;

import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTask;

public class LocalTaskRename extends LocalChange {
    
    private SynchronizableTask newerTask;
    private final SynchronizableTask olderLocalTask;

    public LocalTaskRename(SynchronizableTask olderLocalTask, SynchronizableTask newerTask) {
        if (olderLocalTask == null)
            throw new NullPointerException("olderTask is null");
        if (newerTask == null)
            throw new NullPointerException("task is null");
        this.olderLocalTask = olderLocalTask;
        this.newerTask = newerTask;
    }

    @Override
    public void accept(LocalChangeVisitor visitor) {
        visitor.visitRename(olderLocalTask, newerTask);
    }
 
    @Override
    public String toString() {
        return "renamed " + newerTask;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((newerTask == null) ? 0 : newerTask.hashCode());
        result = prime * result + ((olderLocalTask == null) ? 0 : olderLocalTask.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LocalTaskRename other = (LocalTaskRename) obj;
        if (newerTask == null) {
            if (other.newerTask != null)
                return false;
        } else if (!newerTask.equals(other.newerTask))
            return false;
        if (olderLocalTask == null) {
            if (other.olderLocalTask != null)
                return false;
        } else if (!olderLocalTask.equals(other.olderLocalTask))
            return false;
        return true;
    }

}