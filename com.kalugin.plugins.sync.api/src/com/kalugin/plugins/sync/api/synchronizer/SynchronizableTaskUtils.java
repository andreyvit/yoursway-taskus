package com.kalugin.plugins.sync.api.synchronizer;

import com.google.common.base.Function;

public class SynchronizableTaskUtils {
    
    public static final Function<SynchronizableTask, TaskId> TASK_TO_ID = new Function<SynchronizableTask, TaskId>() {

        public TaskId apply(SynchronizableTask task) {
            return task.getId();
        }
        
    };
    
    public static final Function<SynchronizableTag, String> TAG_TO_NAME = new Function<SynchronizableTag, String>() {
        
        public String apply(SynchronizableTag tag) {
            return tag.getName();
        }
        
    };
    
}
