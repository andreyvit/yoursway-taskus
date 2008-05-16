/**
 * 
 */
package com.kalugin.plugins.sync.api.synchronizer;

import static com.google.common.collect.Lists.newArrayList;
import static com.kalugin.plugins.sync.api.synchronizer.changes.Changes.compareTasks;

import java.util.Collection;
import java.util.List;

import com.kalugin.plugins.sync.api.synchronizer.changes.Addition;
import com.kalugin.plugins.sync.api.synchronizer.changes.Change;
import com.kalugin.plugins.sync.api.synchronizer.changes.ChangeVisitor;
import com.kalugin.plugins.sync.api.synchronizer.changes.Removal;
import com.kalugin.plugins.sync.api.synchronizer.changes.Rename;
import com.kalugin.plugins.sync.api.synchronizer.changes.TagAddition;
import com.kalugin.plugins.sync.api.synchronizer.changes.TagRemoval;
import com.kalugin.plugins.sync.api.synchronizer.changes.TagValueChange;

final class RemotizingVisitor implements ChangeVisitor {
    
    private final Collection<Change> result;
    
    private final List<? extends SynchronizableTask> remoteTasks;
    
    RemotizingVisitor(Collection<Change> result, List<? extends SynchronizableTask> remoteTasks) {
        this.result = result;
        this.remoteTasks = remoteTasks;
    }
    
    public void visitAddition(SynchronizableTask localTask) {
        SynchronizableTask remoteTask = map(localTask);
        if (remoteTask == null) {
            result.add(new Addition(localTask));
        } else {
            Collection<Change> changes = newArrayList();
            compareTasks(remoteTask, localTask, changes);
            for (Change change : changes)
                change.accept(this);
        }
    }
    
    public void visitRemoval(SynchronizableTask localTask) {
        SynchronizableTask remoteTask = map(localTask);
        if (remoteTask != null)
            result.add(new Removal(remoteTask));
    }
    
    public void visitRename(SynchronizableTask olderRemoteTask, SynchronizableTask newerRemoteTask) {
        SynchronizableTask olderLocalTask = map(olderRemoteTask);
        if (olderLocalTask != null)
            result.add(new Rename(olderLocalTask, newerRemoteTask));
        else
            missingTask(newerRemoteTask);
    }
    
    public void visitTagAddition(SynchronizableTask localTask, SynchronizableTag remoteTag) {
        SynchronizableTask remoteTask = map(localTask);
        if (remoteTask == null)
            missingTask(localTask);
        else {
            SynchronizableTag localTag = map(remoteTask, remoteTag);
            if (localTag == null)
                result.add(new TagAddition(remoteTask, remoteTag));
            else if (!localTag.valueEquals(remoteTag))
                result.add(new TagValueChange(remoteTask, localTag, remoteTag));
        }
    }
    
    public void visitTagRemoval(SynchronizableTask localTask, SynchronizableTag remoteTag) {
        SynchronizableTask remoteTask = map(localTask);
        if (remoteTask == null)
            missingTask(localTask);
        else {
            SynchronizableTag localTag = map(remoteTask, remoteTag);
            if (localTag != null)
                result.add(new TagRemoval(remoteTask, localTag));
        }
    }
    
    public void visitTagValueChange(SynchronizableTask localTask, SynchronizableTag olderTag,
            SynchronizableTag newerRemoteTag) {
        SynchronizableTask remoteTask = map(localTask);
        if (remoteTask == null)
            missingTask(localTask);
        else {
            SynchronizableTag localTag = map(remoteTask, newerRemoteTag);
            if (localTag == null)
                result.add(new TagAddition(remoteTask, newerRemoteTag));
            else if (!localTag.valueEquals(newerRemoteTag))
                result.add(new TagValueChange(remoteTask, localTag, newerRemoteTag));
        }
    }
    
    private void missingTask(SynchronizableTask localTask) {
    }

    SynchronizableTask map(SynchronizableTask task) {
        TaskId id = task.getId();
        if (id != null)
            for (SynchronizableTask remoteTask : remoteTasks)
                if (id.equals(remoteTask.getId()))
                    return remoteTask;
        String name = task.getName();
        for (SynchronizableTask remoteTask : remoteTasks)
            if (remoteTask.getId() == null && name.equals(remoteTask.getName()))
                return remoteTask;
        return null;
    }
    
    SynchronizableTag map(SynchronizableTask task, SynchronizableTag tag) {
        String name = tag.getName();
        for (SynchronizableTag theTag : task.tags())
            if (theTag.nameEquals(name))
                return theTag;
        return null;
    }

}