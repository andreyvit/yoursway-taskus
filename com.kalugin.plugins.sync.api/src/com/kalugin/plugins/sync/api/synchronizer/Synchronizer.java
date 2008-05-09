package com.kalugin.plugins.sync.api.synchronizer;


import static com.kalugin.plugins.sync.api.synchronizer.Changes.compare;

import java.util.Collection;
import java.util.List;

public class Synchronizer {
    
    private List<SynchronizableTask> oldLocalTasks;
    private List<SynchronizableTask> oldRemoteTasks;
    private List<SynchronizableTask> newLocalTasks;
    private List<SynchronizableTask> newRemoteTasks;
    
    public void setOldLocalTasks(List<SynchronizableTask> oldLocalTasks) {
        this.oldLocalTasks = oldLocalTasks;
    }
    
    public void setOldRemoteTasks(List<SynchronizableTask> oldRemoteTasks) {
        this.oldRemoteTasks = oldRemoteTasks;
    }
    
    public void setNewLocalTasks(List<SynchronizableTask> newLocalTasks) {
        this.newLocalTasks = newLocalTasks;
    }
    
    public void setNewRemoteTasks(List<SynchronizableTask> newRemoteTasks) {
        this.newRemoteTasks = newRemoteTasks;
    }
    
    public SynchronizationResult synchronize() {
        Collection<Change> localChanges = compare(oldLocalTasks, newLocalTasks);
        Collection<Change> remoteChanges = compare(oldRemoteTasks, newRemoteTasks);
        return new SynchronizationResult(remoteChanges, localChanges);
    }
    
}
