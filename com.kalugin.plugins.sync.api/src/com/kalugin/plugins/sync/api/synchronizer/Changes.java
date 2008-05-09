package com.kalugin.plugins.sync.api.synchronizer;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.uniqueIndex;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;

public class Changes {
    
    private interface ChangesRequestor<T> {
        
        void added(T item);
        
        void removed(T item);
        
        void common(T oldItem, T newItem);
        
    }
    
    public static <T, Id> void compare(Collection<T> older, Collection<T> newer, Function<T, Id> identity, ChangesRequestor<T> requestor) {
        Map<Id, T> idsToOlder = uniqueIndex(older, identity);
        Map<Id, T> idsToNewer = uniqueIndex(newer, identity);
        
        Set<Id> oldSet = newHashSet(idsToOlder.keySet());
        Set<Id> newSet = newHashSet(idsToNewer.keySet());
        
        // removals
        Set<Id> removed = newHashSet(oldSet);
        removed.removeAll(newSet);
        for (T item : older)
            if (removed.contains(identity.apply(item)))
                requestor.removed(item);
        
        // additions
        newSet.removeAll(oldSet);
        for (T item : newer)
            if (newSet.contains(identity.apply(item)))
                requestor.added(item);
        
        // changes
        oldSet.removeAll(removed);
        for (T oldItem : older) {
            Id id = identity.apply(oldItem);
            if (id != null && oldSet.contains(id))
                requestor.common(oldItem, idsToNewer.get(id));
        }
    }
    
    private static class TaskChangesBuilder implements ChangesRequestor<SynchronizableTask> {
        
        private Collection<Change> changes = newArrayList();

        public void added(SynchronizableTask item) {
            changes.add(new Addition(item));
        }

        public void common(SynchronizableTask oldItem, SynchronizableTask newItem) {
            checkRename(oldItem, newItem);
        }

        public void removed(SynchronizableTask item) {
            changes.add(new Removal(item));
        }
        
        public Collection<Change> getChanges() {
            return changes;
        }
        
        private void checkRename(SynchronizableTask olderTask, SynchronizableTask newerTask) {
            String olderName = olderTask.getName();
            String newerName = newerTask.getName();
            if (!olderName.equals(newerName))
                changes.add(new Rename(newerTask));
        }
        
    }
    
    public static Collection<Change> compare(List<SynchronizableTask> older, List<SynchronizableTask> newer) {
        TaskChangesBuilder builder = new TaskChangesBuilder();
        compare(older, newer, SynchronizableTaskUtils.TASK_TO_ID, builder);
        return builder.getChanges();
    }

}
