package com.kalugin.plugins.sync.api;

import static com.google.common.collect.Lists.newArrayList;

import java.util.ArrayList;
import java.util.Collection;

import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTag;
import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTask;
import com.kalugin.plugins.sync.api.synchronizer.TaskId;

public class FakeTask implements SynchronizableTask {
    
    private TaskId id;
    private String name;
    private SynchronizableTag fakeTag;
    private FakeSynchronizableTag idTag;

    public FakeTask(int index, String idTagName) {
        this.id = new TaskId("F" + index);
        this.name = "Fake task " + index;
        if (Math.random() > 0.5)
            fakeTag = new FakeSynchronizableTag("faketag", "V" + (int) (Math.random() * 100));
        idTag = new FakeSynchronizableTag(idTagName, id.stringValue());
    }

    public TaskId getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public Collection<SynchronizableTag> tags() {
        ArrayList<SynchronizableTag> result = newArrayList();
        result.add(idTag);
        if (fakeTag != null)
            result.add(fakeTag);
        return result;
    }
    
    public String toStringWithoutTags() {
        return name + " #" + id;
    }
    
}
