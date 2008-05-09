/**
 * 
 */
package com.kalugin.plugins.sync.api.synchronizer.changes;

import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTag;
import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTask;

public class TagRemoval extends Change {
    
    private final SynchronizableTag tag;
    private final SynchronizableTask task;

    public TagRemoval(SynchronizableTask task, SynchronizableTag tag) {
        if (task == null)
            throw new NullPointerException("task is null");
        if (tag == null)
            throw new NullPointerException("tag is null");
        this.task = task;
        this.tag = tag;
    }

    @Override
    public void accept(ChangeVisitor visitor) {
        visitor.visitTagRemoval(task, tag);
    }
    
    @Override
    public String toString() {
        return "removed " + tag + " from " + task;
    }
    
}