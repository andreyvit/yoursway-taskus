/**
 * 
 */
package com.kalugin.plugins.sync.api.synchronizer.local_changes;

import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTag;
import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTask;

public class LocalTagRemoval extends LocalChange {
    
    private final SynchronizableTag tag;
    private final SynchronizableTask task;

    public LocalTagRemoval(SynchronizableTask task, SynchronizableTag tag) {
        if (task == null)
            throw new NullPointerException("task is null");
        if (tag == null)
            throw new NullPointerException("tag is null");
        this.task = task;
        this.tag = tag;
    }

    @Override
    public void accept(LocalChangeVisitor visitor) {
        visitor.visitTagRemoval(task, tag);
    }
    
    @Override
    public String toString() {
        return "removed " + tag + " from " + task.toStringWithoutTags();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((tag == null) ? 0 : tag.hashCode());
        result = prime * result + ((task == null) ? 0 : task.hashCode());
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
        LocalTagRemoval other = (LocalTagRemoval) obj;
        if (tag == null) {
            if (other.tag != null)
                return false;
        } else if (!tag.equals(other.tag))
            return false;
        if (task == null) {
            if (other.task != null)
                return false;
        } else if (!task.equals(other.task))
            return false;
        return true;
    }
    
}