package com.kalugin.plugins.sync.api.synchronizer;

import java.util.Collection;

import com.kalugin.plugins.sync.api.synchronizer.changes.Change;
import com.kalugin.plugins.sync.api.synchronizer.local_changes.LocalChange;

public class SynchronizationResult {
    
    private final Collection<LocalChange> changesToApplyLocally;
    private final Collection<Change> changesToApplyRemotely;

    public SynchronizationResult(Collection<LocalChange> changesToApplyLocally, Collection<Change> changesToApplyRemotely) {
        if (changesToApplyLocally == null)
            throw new NullPointerException("changesToApplyLocally is null");
        if (changesToApplyRemotely == null)
            throw new NullPointerException("changesToApplyRemotely is null");
        this.changesToApplyLocally = changesToApplyLocally;
        this.changesToApplyRemotely = changesToApplyRemotely;
    }
    
    public Collection<LocalChange> getChangesToApplyLocally() {
        return changesToApplyLocally;
    }
    
    public Collection<Change> getChangesToApplyRemotely() {
        return changesToApplyRemotely;
    }
    
}
