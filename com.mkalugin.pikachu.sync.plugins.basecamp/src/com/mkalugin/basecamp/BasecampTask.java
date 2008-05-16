package com.mkalugin.basecamp;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.List;

import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTag;
import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTask;
import com.kalugin.plugins.sync.api.synchronizer.TaskId;
import com.mkalugin.basecamp.model.ToDoItem;

public class BasecampTask implements SynchronizableTask {
    
    private final ToDoItem item;
    private BasecampTag idTag;
    private BasecampTag doneTag;

    public BasecampTask(ToDoItem item, String idTagName) {
        if (item == null)
            throw new NullPointerException("item is null");
        if (idTagName == null)
            throw new NullPointerException("idTagName is null");
        this.item = item;
        idTag = new BasecampTag(idTagName, "" + item.getId());
        if (item.isCompleted())
            doneTag = new BasecampTag("done", null);
    }

    public TaskId getId() {
        return new TaskId("" + item.getId());
    }

    public String getName() {
        return item.getContent();
    }

    public Collection<SynchronizableTag> tags() {
        List<SynchronizableTag> result = newArrayList();
        result.add(idTag);
        if (doneTag != null)
            result.add(doneTag);
        return result;
    }

    public String toStringWithoutTags() {
        return item.getContent() + " #" + item.getId();
    }
    
}
