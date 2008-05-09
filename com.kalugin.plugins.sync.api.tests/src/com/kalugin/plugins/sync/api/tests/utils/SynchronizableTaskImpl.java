package com.kalugin.plugins.sync.api.tests.utils;

import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTask;
import com.kalugin.plugins.sync.api.synchronizer.TaskId;
import com.kalugin.plugins.sync.api.tests.synchronizer.mocks.IdAssigner;
import com.kalugin.plugins.sync.api.tests.synchronizer.mocks.TaskIdImpl;

public class SynchronizableTaskImpl implements SynchronizableTask {
    
    private final String name;
    private final TaskIdImpl id;

    public SynchronizableTaskImpl(String name, TaskIdImpl id) {
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
        if (id.numericValue() < 100)
            return name + " #" + id;
        else
            return name;
    }

    public static IdAssigner<String> createIdAssigner() {
        return new IdAssigner<String>(100, 1);
    }
    
}
