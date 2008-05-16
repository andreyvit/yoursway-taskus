/**
 * 
 */
package com.kalugin.plugins.sync.api.synchronizer.local_changes;

import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTag;
import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTask;

public class LocalTagAddition extends LocalChange {
    
    private final SynchronizableTag remoteTag;
    private final SynchronizableTask localTask;

    public LocalTagAddition(SynchronizableTask localTask, SynchronizableTag remoteTag) {
        if (localTask == null)
            throw new NullPointerException("task is null");
        if (remoteTag == null)
            throw new NullPointerException("tag is null");
        this.localTask = localTask;
        this.remoteTag = remoteTag;
    }

    @Override
    public void accept(LocalChangeVisitor visitor) {
        visitor.visitTagAddition(localTask, remoteTag);
    }
    
    @Override
    public String toString() {
        return "added " + remoteTag + " to " + localTask.toStringWithoutTags();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((localTask == null) ? 0 : localTask.hashCode());
        result = prime * result + ((remoteTag == null) ? 0 : remoteTag.hashCode());
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
        LocalTagAddition other = (LocalTagAddition) obj;
        if (localTask == null) {
            if (other.localTask != null)
                return false;
        } else if (!localTask.equals(other.localTask))
            return false;
        if (remoteTag == null) {
            if (other.remoteTag != null)
                return false;
        } else if (!remoteTag.equals(other.remoteTag))
            return false;
        return true;
    }
    
}