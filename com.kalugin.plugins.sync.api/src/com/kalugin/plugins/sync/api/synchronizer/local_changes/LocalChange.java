/**
 * 
 */
package com.kalugin.plugins.sync.api.synchronizer.local_changes;

public abstract class LocalChange {
    
    public abstract void accept(LocalChangeVisitor visitor);
    
    @Override
    public abstract int hashCode();
    
    @Override
    public abstract boolean equals(Object obj);
    
}