package com.mkalugin.pikachu.core.controllers.sync;

import static com.google.common.collect.Lists.newArrayList;
import static com.yoursway.utils.YsStrings.sortedToStringUsing;
import static java.util.Collections.unmodifiableCollection;

import java.util.Collection;

import com.google.common.base.Predicate;
import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTag;
import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTask;
import com.kalugin.plugins.sync.api.synchronizer.TaskId;
import com.mkalugin.pikachu.core.model.document.structure.MTag;
import com.mkalugin.pikachu.core.model.document.structure.MTask;

public class LocalTask implements SynchronizableTask {
    
    private final String name;
    private TaskId id;
    private final Collection<SynchronizableTag> tags = newArrayList();
    private final String idTagName;

    private final MTask task;

    public LocalTask(MTask task, String idTagName) {
        if (task == null)
            throw new NullPointerException("task is null");
        if (idTagName == null)
            throw new NullPointerException("idTagName is null");
        this.task = task;
        this.name = task.getName();
        this.idTagName = idTagName;
        for (MTag tag : task.getTags())
            addTag(new LocalTag(tag));
    }

    public TaskId getId() {
        return id;
    }
    
    public boolean hasId() {
        return id != null;
    }

    public String getName() {
        return name;
    }
    
    public MTask getTask() {
        return task;
    }
    
    @Override
    public String toString() {
        return toString(true);
    }
    
    public String toStringWithoutTags() {
        return toString(false);
    }

    private String toString(boolean includeTags) {
        StringBuilder result = new StringBuilder();
        result.append(name);
        result.append(" #").append(id.stringValue());
        if (includeTags && !tags.isEmpty())
            result.append(' ').append(sortedToStringUsing(tags, " "));
        return result.toString();
    }

    public Collection<SynchronizableTag> tags() {
        return unmodifiableCollection(tags);
    }
    
    private void addTag(LocalTag tag) {
        tags.add(tag);
        if(tag.nameEquals(idTagName))
            this.id = new TaskId(tag.getValue());
    }
    
}
