/**
 * 
 */
package com.kalugin.plugins.sync.api.synchronizer;

public class Rename extends Change {
    
    public Rename(SynchronizableTask newerTask) {
        super(newerTask);
    }

    @Override
    public void accept(ChangeVisitor visitor) {
        visitor.visitRename(task);
    }
 
    @Override
    public String toString() {
        return "renamed " + task;
    }

}