/**
 * 
 */
package com.kalugin.plugins.sync.api.synchronizer.changes;

import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTag;
import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTask;

public class TagValueChange extends Change {
    
    private final SynchronizableTag newTag;
    private final SynchronizableTask task;
    private final SynchronizableTag oldTag;

    public TagValueChange(SynchronizableTask task, SynchronizableTag oldTag, SynchronizableTag newTag) {
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
    public void accept(ChangeVisitor visitor) {
        visitor.visitTagValueChange(task, oldTag, newTag);
    }
    
    @Override
    public String toString() {
        return "changed " + newTag + " in " + task.toStringWithoutTags();
    }
    
}