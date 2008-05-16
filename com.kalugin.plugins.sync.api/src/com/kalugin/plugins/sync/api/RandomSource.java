package com.kalugin.plugins.sync.api;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTask;

public class RandomSource implements Source {
    
    public List<SynchronizableTask> computeTasks() {
        List<SynchronizableTask> result = newArrayList();
        for (int i = 0; i < 10; i++) 
            result.add(new FakeTask(i, idTagName()));
        if (Math.random() > 0.5)
            result.add(new FakeTask(42, idTagName()));
        return result;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    public String idTagName() {
        return "randomid";
    }

    public String identifier() {
        return getClass().getSimpleName();
    }
    
}
