/**
 * 
 */
package com.kalugin.plugins.sync.api.synchronizer.local_changes;

import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTask;

public class LocalTaskRemoval extends LocalChange {

    private final SynchronizableTask localTask;

    public LocalTaskRemoval(SynchronizableTask localTask) {
        if (localTask == null)
            throw new NullPointerException("localTask is null");
        this.localTask = localTask;
    }

    @Override
    public void accept(LocalChangeVisitor visitor) {
        visitor.visitTaskRemoval(localTask);
    }
    
    @Override
    public String toString() {
        return "removed " + localTask;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((localTask == null) ? 0 : localTask.hashCode());
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
        LocalTaskRemoval other = (LocalTaskRemoval) obj;
        if (localTask == null) {
            if (other.localTask != null)
                return false;
        } else if (!localTask.equals(other.localTask))
            return false;
        return true;
    }

}