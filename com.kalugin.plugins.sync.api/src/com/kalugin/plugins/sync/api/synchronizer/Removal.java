/**
 * 
 */
package com.kalugin.plugins.sync.api.synchronizer;

public class Removal extends Change {

    public Removal(SynchronizableTask task) {
        super(task);
    }

    @Override
    public void accept(ChangeVisitor visitor) {
        visitor.visitRemoval(task);
    }
    
    @Override
    public String toString() {
        return "removed " + task;
    }

}