package com.kalugin.plugins.sync.api.synchronizer.local_changes;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.uniqueIndex;
import static com.google.common.collect.Sets.newHashSet;
import static com.kalugin.plugins.sync.api.synchronizer.SynchronizableTaskUtils.TAG_TO_NAME;
import static com.kalugin.plugins.sync.api.synchronizer.SynchronizableTaskUtils.TASK_TO_ID;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTag;
import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTask;

public class Changes {
    
    private interface ChangesRequestor<T> {
        
        void added(T item);
        
        void removed(T item);
        
        void common(T oldItem, T newItem);
        
    }
    
    public static <T, Id> void compare(Collection<? extends T> older, Collection<? extends T> newer, Function<T, Id> identity, ChangesRequestor<T> requestor) {
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
        
        private Collection<LocalChange> localChanges = newArrayList();

        public void added(SynchronizableTask item) {
            localChanges.add(new LocalTaskAddition(item));
        }

        public void common(SynchronizableTask oldItem, SynchronizableTask newItem) {
            checkRename(oldItem, newItem);
            checkTagChanges(oldItem, newItem);
        }

        public void removed(SynchronizableTask item) {
            localChanges.add(new LocalTaskRemoval(item));
        }
        
        public Collection<LocalChange> getChanges() {
            return localChanges;
        }
        
        private void checkRename(SynchronizableTask olderTask, SynchronizableTask newerTask) {
            String olderName = olderTask.getName();
            String newerName = newerTask.getName();
            if (!olderName.equals(newerName))
                localChanges.add(new LocalTaskRename(olderTask, newerTask));
        }
        
        private void checkTagChanges(SynchronizableTask olderTask, SynchronizableTask newerTask) {
            compare(olderTask.tags(), newerTask.tags(), TAG_TO_NAME, new TagChangesBuilder(newerTask, localChanges));
        }
        
    }
    
    private static class TagChangesBuilder implements ChangesRequestor<SynchronizableTag> {
        
        private final Collection<LocalChange> localChanges;
        private final SynchronizableTask task;

        public TagChangesBuilder(SynchronizableTask task, Collection<LocalChange> localChanges) {
            if (task == null)
                throw new NullPointerException("task is null");
            if (localChanges == null)
                throw new NullPointerException("changes is null");
            this.task = task;
            this.localChanges = localChanges;
        }

        public void added(SynchronizableTag item) {
            localChanges.add(new LocalTagAddition(task, item));
        }

        public void common(SynchronizableTag oldItem, SynchronizableTag newItem) {
            if (!newItem.valueEquals(oldItem))
                localChanges.add(new LocalTagValueChange(task, oldItem, newItem));
        }

        public void removed(SynchronizableTag item) {
            localChanges.add(new LocalTagRemoval(task, item));
        }
        
    }
    
    public static final Predicate<SynchronizableTask> HAS_ID = new Predicate<SynchronizableTask>() {

        public boolean apply(SynchronizableTask t) {
            return t.getId() != null;
        }
        
    };

    public static Collection<LocalChange> compare(List<? extends SynchronizableTask> older, List<? extends SynchronizableTask> newer) {
        TaskChangesBuilder builder = new TaskChangesBuilder();
        compare(newArrayList(filter(older, HAS_ID)), newArrayList(filter(newer, HAS_ID)), TASK_TO_ID, builder);
        return builder.getChanges();
    }

}
