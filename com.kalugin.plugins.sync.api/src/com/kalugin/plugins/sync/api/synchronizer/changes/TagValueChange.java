/**
 * 
 */
package com.kalugin.plugins.sync.api.synchronizer.changes;

import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTag;
import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTask;

public class TagValueChange extends Change {
    
    private final SynchronizableTag newTag;
    private final SynchronizableTask task;

    public TagValueChange(SynchronizableTask task, SynchronizableTag newTag) {
        if (task == null)
            throw new NullPointerException("task is null");
        if (newTag == null)
            throw new NullPointerException("tag is null");
        this.task = task;
        this.newTag = newTag;
    }

    @Override
    public void accept(ChangeVisitor visitor) {
        visitor.visitTagValueChange(task, newTag);
    }
    
    @Override
    public String toString() {
        return "changed " + newTag + " in " + task.toStringWithoutTags();
    }
    
}