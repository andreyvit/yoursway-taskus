/**
 * 
 */
package com.kalugin.plugins.sync.api.synchronizer.changes;

import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTag;
import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTask;

public class TagAddition extends Change {
    
    private final SynchronizableTag tag;
    private final SynchronizableTask task;

    public TagAddition(SynchronizableTask task, SynchronizableTag tag) {
        if (task == null)
            throw new NullPointerException("task is null");
        if (tag == null)
            throw new NullPointerException("tag is null");
        this.task = task;
        this.tag = tag;
    }

    @Override
    public void accept(ChangeVisitor visitor) {
        visitor.visitTagAddition(task, tag);
    }
    
    @Override
    public String toString() {
        return "added " + tag + " to " + task.toStringWithoutTags();
    }
    
}