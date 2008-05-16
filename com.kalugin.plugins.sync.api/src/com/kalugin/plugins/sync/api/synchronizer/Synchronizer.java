package com.kalugin.plugins.sync.api.synchronizer;

import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.newArrayList;
import static com.kalugin.plugins.sync.api.synchronizer.RemovalCheckVisitor.IS_REMOVAL;
import static com.kalugin.plugins.sync.api.synchronizer.changes.Changes.compare;

import java.util.Collection;
import java.util.List;

import com.kalugin.plugins.sync.api.synchronizer.changes.Addition;
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
    
    public Collection<Change> synchronizeRemote() {
        Collection<Change> changesToApplyRemotely = calculateChangesToApplyRemotely();
        return remotize(newRemoteTasks, changesToApplyRemotely);
    }
    
    public Collection<LocalChange> synchronizeLocal() {
        Collection<Change> changesToApplyLocally = calculateChangesToApplyLocally();
        return localize(newLocalTasks, changesToApplyLocally);
    }

    private Collection<Change> calculateChangesToApplyLocally() {
        Collection<Change> changesToApplyLocally;
        if (oldRemoteTasks != null)
            changesToApplyLocally = compare(oldRemoteTasks, newRemoteTasks);
        else {
            Collection<Change> changes = compare(newLocalTasks, newRemoteTasks);
            changesToApplyLocally = newArrayList(filter(changes, not(IS_REMOVAL)));
        }
        return changesToApplyLocally;
    }

    private Collection<Change> calculateChangesToApplyRemotely() {
        Collection<Change> changesToApplyRemotely;
        if (oldLocalTasks != null)
            changesToApplyRemotely = compare(oldLocalTasks, newLocalTasks);
        else
            changesToApplyRemotely = newArrayList();
        addTasksThatWantToBeAdded(changesToApplyRemotely);
        return changesToApplyRemotely;
    }

    private void addTasksThatWantToBeAdded(Collection<Change> changesToApplyRemotely) {
        for (SynchronizableTask task : newLocalTasks)
            if (task.getId() == null && task.wannaBeAdded())
                changesToApplyRemotely.add(new Addition(task));
    }
    
    private Collection<LocalChange> localize(List<? extends SynchronizableTask> localTasks,
            Collection<Change> changes) {
        final Collection<LocalChange> result = newArrayList();
        for (Change change : changes)
            change.accept(new LocalizingVisitor(result, localTasks));
        return result;
    }
    
    private Collection<Change> remotize(List<? extends SynchronizableTask> remoteTasks,
            Collection<Change> changes) {
        final Collection<Change> result = newArrayList();
        for (Change change : changes)
            change.accept(new RemotizingVisitor(result, remoteTasks));
        return result;
    }
    
}
