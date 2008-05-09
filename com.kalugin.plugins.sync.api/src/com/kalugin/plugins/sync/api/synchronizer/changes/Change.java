/**
 * 
 */
package com.kalugin.plugins.sync.api.synchronizer.changes;

public abstract class Change {
    
    public abstract void accept(ChangeVisitor visitor);
    
}