/**
 * 
 */
package com.kalugin.plugins.sync.api.synchronizer;

public class Addition extends Change {
    
    public Addition(SynchronizableTask task) {
        super(task);
    }

    @Override
    public void accept(ChangeVisitor visitor) {
        visitor.visitAddition(task);
    }
    
    @Override
    public String toString() {
        return "added " + task;
    }
    
}