package com.kalugin.plugins.sync.api.synchronizer;

import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static com.kalugin.plugins.sync.api.synchronizer.RemovalCheckVisitor.IS_REMOVAL;
import static com.kalugin.plugins.sync.api.synchronizer.TaskRemovalCheckVisitor.IS_TASK_REMOVAL;
import static com.kalugin.plugins.sync.api.synchronizer.changes.Changes.compare;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.kalugin.plugins.sync.api.synchronizer.changes.Addition;
import com.kalugin.plugins.sync.api.synchronizer.changes.Change;
import com.kalugin.plugins.sync.api.synchronizer.changes.ChangeVisitor;
import com.kalugin.plugins.sync.api.synchronizer.changes.Removal;
import com.kalugin.plugins.sync.api.synchronizer.local_changes.LocalChange;
import com.kalugin.plugins.sync.api.synchronizer.local_changes.LocalChangeVisitor;
import com.kalugin.plugins.sync.api.synchronizer.local_changes.LocalTaskAddition;

public class Synchronizer {
    
    private final class TaskAdditionMatcher implements LocalChangeVisitor {
        
        private final SynchronizableTask task;
        
        private boolean matched = false;
        
        private TaskAdditionMatcher(SynchronizableTask task) {
            this.task = task;
        }
        
        public boolean hasMatched() {
            return matched;
        }
        
        public void visitAddition(SynchronizableTask remoteTask) {
            if (remoteTask.getId() != null
                && remoteTask.getId().equals(task.getId()))
                matched = true;
        }
        
        public void visitIgnoredTaskAddition(SynchronizableTask remoteTask) {
        }
        
        public void visitRename(SynchronizableTask olderLocalTask,
                SynchronizableTask newerRemoteTask) {
        }
        
        public void visitTagAddition(SynchronizableTask localTask,
                SynchronizableTag remoteTag) {
        }
        
        public void visitTagRemoval(SynchronizableTask localTask,
                SynchronizableTag localTag) {
        }
        
        public void visitTagValueChange(SynchronizableTask localTask,
                SynchronizableTag olderLocalTag, SynchronizableTag newerRemoteTag) {
        }
        
        public void visitTaskRemoval(SynchronizableTask localTask) {
        }
    }

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
        addTasksThatWantToBeAdded(changesToApplyRemotely);
        return remotize(newRemoteTasks, ignorePlainRemovalsAndRemoveTasksTaggedWithRemove(changesToApplyRemotely));
    }

    private Collection<Change> ignorePlainRemovalsAndRemoveTasksTaggedWithRemove(
            Collection<Change> changesToApplyRemotely) {
        changesToApplyRemotely = newArrayList(filter(changesToApplyRemotely, not(IS_TASK_REMOVAL)));
        remoteTasksThatWantToBeRemoved(changesToApplyRemotely);
        return changesToApplyRemotely;
    }
    
    public Collection<LocalChange> synchronizeLocal() {
        Collection<LocalChange> changesToApplyLocally = newArrayList();
        LocalizingVisitor localizer = new LocalizingVisitor(changesToApplyLocally, newLocalTasks);
        if (oldRemoteTasks != null) {
            traverse(compare(oldRemoteTasks, newRemoteTasks), localizer);
            // also add all missing tags and missing tasks
            traverse(compare(newLocalTasks, newRemoteTasks), new MissingTasksVisitor(localizer));
        } else {
            traverse(filter(compare(newLocalTasks, newRemoteTasks), not(IS_REMOVAL)), localizer);
        }
        return removeOverlappingLocalChanges(removeDuplicates(changesToApplyLocally));
    }

    private HashSet<LocalChange> removeDuplicates(Collection<LocalChange> changesToApplyLocally) {
        return newHashSet(changesToApplyLocally);
    }

    private List<LocalChange> removeOverlappingLocalChanges(final Collection<LocalChange> changes) {
        final List<LocalChange> result = newArrayList();
        for (final LocalChange change : changes) 
            change.accept(new LocalChangeVisitor() {

                public void visitAddition(SynchronizableTask remoteTask) {
                    result.add(change);
                }

                public void visitIgnoredTaskAddition(SynchronizableTask remoteTask) {
                    if (!containsTaskAddition(remoteTask))
                        result.add(change);
                }

                public void visitRename(SynchronizableTask olderLocalTask, SynchronizableTask newerRemoteTask) {
                    if (!containsTaskAddition(newerRemoteTask))
                        result.add(change);
                }

                public void visitTagAddition(SynchronizableTask localTask, SynchronizableTag remoteTag) {
                    if (!containsTaskAddition(localTask))
                        result.add(change);
                }

                public void visitTagRemoval(SynchronizableTask localTask, SynchronizableTag localTag) {
                    if (!containsTaskAddition(localTask))
                        result.add(change);
                }

                public void visitTagValueChange(SynchronizableTask localTask,
                        SynchronizableTag olderLocalTag, SynchronizableTag newerRemoteTag) {
                    if (!containsTaskAddition(localTask))
                        result.add(change);
                }

                public void visitTaskRemoval(SynchronizableTask localTask) {
                    result.add(change);
                }

                private boolean containsTaskAddition(final SynchronizableTask task) {
                    TaskAdditionMatcher visitor = new TaskAdditionMatcher(task);
                    for (LocalChange change : changes)
                        change.accept(visitor);
                    return visitor.hasMatched();
                }

            });
        return result;
    }

    private Collection<Change> calculateChangesToApplyRemotely() {
        Collection<Change> changesToApplyRemotely;
        if (oldLocalTasks != null)
            changesToApplyRemotely = compare(oldLocalTasks, newLocalTasks);
        else
            changesToApplyRemotely = newArrayList();
        return changesToApplyRemotely;
    }

    private void addTasksThatWantToBeAdded(Collection<Change> changesToApplyRemotely) {
        for (SynchronizableTask task : newLocalTasks)
            if (task.getId() == null && task.wannaBeAdded())
                changesToApplyRemotely.add(new Addition(task));
    }
    
    private void remoteTasksThatWantToBeRemoved(Collection<Change> changesToApplyRemotely) {
        for (SynchronizableTask task : newLocalTasks)
            if (hasTag(task, "remove"))
                changesToApplyRemotely.add(new Removal(task));
    }
    
    private boolean hasTag(SynchronizableTask task, String tagName) {
        for (SynchronizableTag tag : task.tags())
            if (tag.nameEquals(tagName))
                return true;
        return false;
    }
    
    private void traverse(Iterable<Change> changes, ChangeVisitor visitor) {
        for (Change change : changes)
            change.accept(visitor);
    }
    
    private Collection<Change> remotize(List<? extends SynchronizableTask> remoteTasks,
            Collection<Change> changes) {
        final Collection<Change> result = newArrayList();
        for (Change change : changes)
            change.accept(new RemotizingVisitor(result, remoteTasks));
        return result;
    }
    
}
