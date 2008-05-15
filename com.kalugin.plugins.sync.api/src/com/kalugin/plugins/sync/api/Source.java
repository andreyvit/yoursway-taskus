package com.kalugin.plugins.sync.api;

import java.util.List;

import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTask;

public interface Source {
    
    List<SynchronizableTask> computeTasks();
    
    String idTagName();
    
}
