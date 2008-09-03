package com.mkalugin.lighthouse.model;

public class Project {
    
    private final Id id;
    private final String name;

    public Project(Id id, String name) {
        if (name == null)
            throw new NullPointerException("name is null");
        this.id = id;
        this.name = name;
    }
    
    public Id getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
}
