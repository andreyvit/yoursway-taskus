/**
 * 
 */
package com.kalugin.plugins.sync.api.synchronizer.local_changes;

import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTag;
import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTask;

public class LocalTagValueChange extends LocalChange {
    
    private final SynchronizableTag newTag;
    private final SynchronizableTask task;
    private final SynchronizableTag oldTag;

    public LocalTagValueChange(SynchronizableTask task, SynchronizableTag oldTag, SynchronizableTag newTag) {
        if (task == null)
            throw new NullPointerException("task is null");
        if (oldTag == null)
            throw new NullPointerException("oldTag is null");
        if (newTag == null)
            throw new NullPointerException("tag is null");
        this.task = task;
        this.oldTag = oldTag;
        this.newTag = newTag;
    }

    @Override
    public void accept(LocalChangeVisitor visitor) {
        visitor.visitTagValueChange(task, oldTag, newTag);
    }
    
    @Override
    public String toString() {
        return "changed " + newTag + " in " + task.toStringWithoutTags();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((newTag == null) ? 0 : newTag.hashCode());
        result = prime * result + ((oldTag == null) ? 0 : oldTag.hashCode());
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
        LocalTagValueChange other = (LocalTagValueChange) obj;
        if (newTag == null) {
            if (other.newTag != null)
                return false;
        } else if (!newTag.equals(other.newTag))
            return false;
        if (oldTag == null) {
            if (other.oldTag != null)
                return false;
        } else if (!oldTag.equals(other.oldTag))
            return false;
        if (task == null) {
            if (other.task != null)
                return false;
        } else if (!task.equals(other.task))
            return false;
        return true;
    }
    
}