package com.kalugin.plugins.sync.api.synchronizer;

import java.util.Collection;

import com.kalugin.plugins.sync.api.synchronizer.changes.Change;

public class SynchronizationResult {
    
    private final Collection<Change> changesToApplyLocally;
    private final Collection<Change> changesToApplyRemotely;

    public SynchronizationResult(Collection<Change> changesToApplyLocally, Collection<Change> changesToApplyRemotely) {
        if (changesToApplyLocally == null)
            throw new NullPointerException("changesToApplyLocally is null");
        if (changesToApplyRemotely == null)
            throw new NullPointerException("changesToApplyRemotely is null");
        this.changesToApplyLocally = changesToApplyLocally;
        this.changesToApplyRemotely = changesToApplyRemotely;
    }
    
    public Collection<Change> getChangesToApplyLocally() {
        return changesToApplyLocally;
    }
    
    public Collection<Change> getChangesToApplyRemotely() {
        return changesToApplyRemotely;
    }
    
}
