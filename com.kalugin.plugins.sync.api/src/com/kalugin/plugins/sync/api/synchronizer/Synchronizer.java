package com.kalugin.plugins.sync.api.synchronizer;

import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.newArrayList;
import static com.kalugin.plugins.sync.api.synchronizer.RemovalCheckVisitor.IS_REMOVAL;
import static com.kalugin.plugins.sync.api.synchronizer.changes.Changes.compare;

import java.util.Collection;
import java.util.List;

import com.kalugin.plugins.sync.api.synchronizer.changes.Change;
import com.kalugin.plugins.sync.api.synchronizer.local_changes.LocalChange;

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
        Collection<Change> changesToApplyLocally;
        if (oldRemoteTasks != null)
            changesToApplyLocally = compare(oldRemoteTasks, newRemoteTasks);
        else {
            Collection<Change> changes = compare(newLocalTasks, newRemoteTasks);
            changesToApplyLocally = newArrayList(filter(changes, not(IS_REMOVAL)));
        }
        Collection<LocalChange> changesToApplyLocally2 = 
            localize(newLocalTasks, changesToApplyLocally);
        Collection<Change> changesToApplyRemotely;
        if (oldLocalTasks != null)
            changesToApplyRemotely = compare(oldLocalTasks, newLocalTasks);
        else
            changesToApplyRemotely = newArrayList();
        return new SynchronizationResult(changesToApplyLocally2, changesToApplyRemotely);
    }

    private Collection<LocalChange> localize(List<? extends SynchronizableTask> localTasks,
            Collection<Change> changes) {
        final Collection<LocalChange> result = newArrayList();
        for (Change change : changes)
            change.accept(new LocalizingVisitor(result, localTasks));
        return result;
    }
    
}
