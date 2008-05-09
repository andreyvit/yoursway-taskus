package com.kalugin.plugins.sync.api.tests.synchronizer.mocks;

import com.kalugin.plugins.sync.api.synchronizer.TaskId;

public class TaskIdImpl implements TaskId {
    
    private final int id;

    public TaskIdImpl(int id) {
        this.id = id;
    }
    
    public int numericValue() {
        return id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TaskIdImpl other = (TaskIdImpl) obj;
        if (id != other.id)
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return "" + id;
    }
    
}
