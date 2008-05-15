package com.kalugin.plugins.sync.api.synchronizer;

import static com.kalugin.plugins.sync.api.synchronizer.changes.Changes.compare;

import java.util.Collection;
import java.util.List;

import com.kalugin.plugins.sync.api.synchronizer.changes.Change;

public class Synchronizer {
    
    private List<? extends SynchronizableTask> oldLocalTasks;
    private List<? extends SynchronizableTask> oldRemoteTasks;
    private List<? extends SynchronizableTask> newLocalTasks;
    private List<? extends SynchronizableTask> newRemoteTasks;
    
    public void setOldLocalTasks(List<? extends SynchronizableTask> oldLocalTasks) {
        this.oldLocalTasks = oldLocalTasks;
    }
    
    public void setOldRemoteTasks(List<? extends SynchronizableTask> oldRemoteTasks) {
        this.oldRemoteTasks = oldRemoteTasks;
    }
    
    public void setNewLocalTasks(List<? extends SynchronizableTask> newLocalTasks) {
        this.newLocalTasks = newLocalTasks;
    }
    
    public void setNewRemoteTasks(List<? extends SynchronizableTask> newRemoteTasks) {
        this.newRemoteTasks = newRemoteTasks;
    }
    
    public SynchronizationResult synchronize() {
        Collection<Change> localChanges = compare(oldLocalTasks, newLocalTasks);
        Collection<Change> remoteChanges = compare(oldRemoteTasks, newRemoteTasks);
        return new SynchronizationResult(remoteChanges, localChanges);
    }
    
}
