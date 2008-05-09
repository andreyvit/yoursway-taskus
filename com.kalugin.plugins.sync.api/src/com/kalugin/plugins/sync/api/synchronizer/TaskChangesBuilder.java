package com.kalugin.plugins.sync.api.synchronizer;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.uniqueIndex;
import static com.google.common.collect.Sets.newHashSet;
import static com.kalugin.plugins.sync.api.synchronizer.SynchronizableTaskUtils.TASK_TO_ID;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TaskChangesBuilder {

    public static Collection<Change> compare(List<SynchronizableTask> older, List<SynchronizableTask> newer) {
        Map<TaskId, SynchronizableTask> idsToOlderTasks = uniqueIndex(older, TASK_TO_ID);
        Map<TaskId, SynchronizableTask> idsToNewerTasks = uniqueIndex(newer, TASK_TO_ID);
        
        Set<TaskId> oldSet = newHashSet(idsToOlderTasks.keySet());
        Set<TaskId> newSet = newHashSet(idsToNewerTasks.keySet());
        Collection<Change> result = newArrayList();
        
        // removals
        Set<TaskId> removed = newHashSet(oldSet);
        removed.removeAll(newSet);
        
        for (SynchronizableTask task : older)
            if (removed.contains(task.getId()))
                result.add(new Removal(task));
        
        // additions
        newSet.removeAll(oldSet);
        for (SynchronizableTask task : newer)
            if (newSet.contains(task.getId()))
                result.add(new Addition(task));
        
        // renames
        oldSet.removeAll(removed);
        for (SynchronizableTask olderTask : older) {
            TaskId id = olderTask.getId();
            if (id != null && oldSet.contains(id))
                compare(olderTask, idsToNewerTasks.get(id), result);
        }
        return result;
    }

    public static void compare(SynchronizableTask olderTask, SynchronizableTask newerTask, Collection<Change> result) {
        String olderName = olderTask.getName();
        String newerName = newerTask.getName();
        if (!olderName.equals(newerName))
            result.add(new Rename(newerTask));
    }
    
}
