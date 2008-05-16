package com.mkalugin.basecamp;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;

import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTag;
import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTask;
import com.kalugin.plugins.sync.api.synchronizer.TaskId;
import com.mkalugin.basecamp.model.ToDoItem;

public class BasecampTask implements SynchronizableTask {
    
    private final ToDoItem item;
    private BasecampTag idTag;

    public BasecampTask(ToDoItem item, String idTagName) {
        if (item == null)
            throw new NullPointerException("item is null");
        if (idTagName == null)
            throw new NullPointerException("idTagName is null");
        this.item = item;
        idTag = new BasecampTag(idTagName, "" + item.getId());
    }

    public TaskId getId() {
        return new TaskId("" + item.getId());
    }

    public String getName() {
        return item.getContent();
    }

    public Collection<SynchronizableTag> tags() {
        return newArrayList((SynchronizableTag) idTag);
    }

    public String toStringWithoutTags() {
        return item.getContent() + " #" + item.getId();
    }
    
}
