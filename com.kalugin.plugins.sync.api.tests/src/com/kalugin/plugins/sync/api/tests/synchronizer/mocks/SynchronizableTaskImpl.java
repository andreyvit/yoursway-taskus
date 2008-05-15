package com.kalugin.plugins.sync.api.tests.synchronizer.mocks;

import static com.google.common.collect.Lists.newArrayList;
import static com.yoursway.utils.YsStrings.sortedToStringUsing;
import static java.util.Collections.unmodifiableCollection;

import java.util.Collection;
import java.util.Iterator;

import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTag;
import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTask;
import com.kalugin.plugins.sync.api.synchronizer.TaskId;
import com.kalugin.plugins.sync.api.tests.utils.IdAssigner;

public class SynchronizableTaskImpl implements SynchronizableTask {
    
    private final String name;
    private final TaskId id;
    private final Collection<SynchronizableTag> tags = newArrayList();

    public SynchronizableTaskImpl(String name, TaskId id) {
        if (name == null)
            throw new NullPointerException("name is null");
        if (id == null) 
            throw new NullPointerException("id is null");
        this.name = name;
        this.id = id;
    }

    public TaskId getId() {
        return id;
    }

    public String getName() {
        return name;
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
        if (Integer.valueOf(id.stringValue()) < 100)
            result.append(" #").append(id.stringValue());
        if (includeTags && !tags.isEmpty())
            result.append(' ').append(sortedToStringUsing(tags, " "));
        return result.toString();
    }

    public static IdAssigner<String> createIdAssigner() {
        return new IdAssigner<String>(100, 1);
    }

    public Collection<SynchronizableTag> tags() {
        return unmodifiableCollection(tags);
    }
    
    public void addTag(SynchronizableTag tag) {
        tags.add(tag);
    }
    
    public SynchronizableTaskImpl removeTag(String tagName) {
        for (Iterator<SynchronizableTag> iterator = tags.iterator(); iterator.hasNext();) {
            if (iterator.next().nameEquals(tagName)) {
                iterator.remove();
                return this;
            }
        }
        throw new IllegalArgumentException("Tag with name " + tagName + " not found.");
    }
    
}
