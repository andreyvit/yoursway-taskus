package com.mkalugin.basecamp.model;

public abstract class AbstractId {
    
    private final int id;

    public AbstractId(int id) {
        this.id = id;
    }
    
    public int numericId() {
        return id;
    }
    
}
