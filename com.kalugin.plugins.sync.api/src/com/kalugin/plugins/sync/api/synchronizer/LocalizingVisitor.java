/**
 * 
 */
package com.kalugin.plugins.sync.api.synchronizer;

import static com.google.common.collect.Lists.newArrayList;
import static com.kalugin.plugins.sync.api.synchronizer.changes.Changes.compareTasks;

import java.util.Collection;
import java.util.List;

import com.kalugin.plugins.sync.api.synchronizer.changes.Change;
import com.kalugin.plugins.sync.api.synchronizer.changes.ChangeVisitor;
import com.kalugin.plugins.sync.api.synchronizer.local_changes.LocalIgnoredTaskAddition;
import com.kalugin.plugins.sync.api.synchronizer.local_changes.LocalTaskAddition;
import com.kalugin.plugins.sync.api.synchronizer.local_changes.LocalChange;
import com.kalugin.plugins.sync.api.synchronizer.local_changes.LocalTagAddition;
import com.kalugin.plugins.sync.api.synchronizer.local_changes.LocalTagRemoval;
import com.kalugin.plugins.sync.api.synchronizer.local_changes.LocalTagValueChange;
import com.kalugin.plugins.sync.api.synchronizer.local_changes.LocalTaskRemoval;
import com.kalugin.plugins.sync.api.synchronizer.local_changes.LocalTaskRename;

final class LocalizingVisitor implements ChangeVisitor {
    
    private final Collection<LocalChange> result;
    
    private final List<? extends SynchronizableTask> localTasks;
    
    LocalizingVisitor(Collection<LocalChange> result, List<? extends SynchronizableTask> localTasks) {
        this.result = result;
        this.localTasks = localTasks;
    }
    
    public void visitAddition(SynchronizableTask remoteTask) {
        SynchronizableTask localTask = map(remoteTask);
        if (localTask == null) {
            result.add(new LocalTaskAddition(remoteTask));
        } else {
            Collection<Change> changes = newArrayList();
            compareTasks(localTask, remoteTask, changes);
            for (Change change : changes)
                change.accept(this);
        }
    }
    
    public void visitRemoval(SynchronizableTask remoteTask) {
        SynchronizableTask localTask = map(remoteTask);
        if (localTask != null)
            result.add(new LocalTaskRemoval(localTask));
    }
    
    public void visitRename(SynchronizableTask olderRemoteTask, SynchronizableTask newerRemoteTask) {
        SynchronizableTask olderLocalTask = map(olderRemoteTask);
        if (olderLocalTask != null)
            result.add(new LocalTaskRename(olderLocalTask, newerRemoteTask));
        else
            missingTask(newerRemoteTask);
    }
    
    public void visitTagAddition(SynchronizableTask remoteTask, SynchronizableTag remoteTag) {
        SynchronizableTask localTask = map(remoteTask);
        if (localTask == null)
            missingTask(remoteTask);
        else {
            SynchronizableTag localTag = map(localTask, remoteTag);
            if (localTag == null)
                result.add(new LocalTagAddition(localTask, remoteTag));
            else if (!localTag.valueEquals(remoteTag))
                result.add(new LocalTagValueChange(localTask, localTag, remoteTag));
        }
    }
    
    public void visitTagRemoval(SynchronizableTask remoteTask, SynchronizableTag remoteTag) {
        SynchronizableTask localTask = map(remoteTask);
        if (localTask == null)
            missingTask(remoteTask);
        else {
            SynchronizableTag localTag = map(localTask, remoteTag);
            if (localTag != null)
                result.add(new LocalTagRemoval(localTask, localTag));
        }
    }
    
    public void visitTagValueChange(SynchronizableTask remoteTask, SynchronizableTag olderTag,
            SynchronizableTag newerRemoteTag) {
        SynchronizableTask localTask = map(remoteTask);
        if (localTask == null)
            missingTask(remoteTask);
        else {
            SynchronizableTag localTag = map(localTask, newerRemoteTag);
            if (localTag == null)
                result.add(new LocalTagAddition(localTask, newerRemoteTag));
            else if (!localTag.valueEquals(newerRemoteTag))
                result.add(new LocalTagValueChange(localTask, localTag, newerRemoteTag));
        }
    }
    
    private void missingTask(SynchronizableTask remoteTask) {
        result.add(new LocalIgnoredTaskAddition(remoteTask));
    }

    SynchronizableTask map(SynchronizableTask task) {
        TaskId id = task.getId();
        if (id != null)
            for (SynchronizableTask localTask : localTasks)
                if (id.equals(localTask.getId()))
                    return localTask;
        String name = task.getName();
        for (SynchronizableTask localTask : localTasks)
            if (localTask.getId() == null && name.equals(localTask.getName()))
                return localTask;
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