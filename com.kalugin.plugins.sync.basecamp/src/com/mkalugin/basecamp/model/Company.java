package com.mkalugin.basecamp.model;

public class Company {
    
    private final int id;
    private final String name;

    public Company(int id, String name) {
        if (name == null)
            throw new NullPointerException("name is null");
        this.id = id;
        this.name = name;
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
}
