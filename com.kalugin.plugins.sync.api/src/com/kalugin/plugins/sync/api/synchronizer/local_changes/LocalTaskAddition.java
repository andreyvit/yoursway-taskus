/**
 * 
 */
package com.kalugin.plugins.sync.api.synchronizer.local_changes;

import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTask;

public class LocalTaskAddition extends LocalChange {
    
    private final SynchronizableTask remoteTask;
    
    public LocalTaskAddition(SynchronizableTask remoteTask) {
        if (remoteTask == null)
            throw new NullPointerException("task is null");
        this.remoteTask = remoteTask;
    }

    @Override
    public void accept(LocalChangeVisitor visitor) {
        visitor.visitAddition(remoteTask);
    }
    
    @Override
    public String toString() {
        return "added " + remoteTask;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((remoteTask == null) ? 0 : remoteTask.hashCode());
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
        LocalTaskAddition other = (LocalTaskAddition) obj;
        if (remoteTask == null) {
            if (other.remoteTask != null)
                return false;
        } else if (!remoteTask.equals(other.remoteTask))
            return false;
        return true;
    }
    
}