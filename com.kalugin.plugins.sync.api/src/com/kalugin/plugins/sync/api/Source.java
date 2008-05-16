package com.kalugin.plugins.sync.api;

import java.util.Collection;
import java.util.List;

import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTask;
import com.kalugin.plugins.sync.api.synchronizer.changes.Change;

public interface Source {
    
    List<SynchronizableTask> computeTasks(SourceCallback callback);
    
    String idTagName();

    String identifier();

    void applyChanges(Collection<Change> changesToApplyRemotely, SourceCallback callback);

    void dispose();
    
}
