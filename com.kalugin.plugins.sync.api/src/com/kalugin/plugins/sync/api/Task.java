package com.kalugin.plugins.sync.api;

public class Task {
    
    private final String name;

    public Task(String name) {
        if (name == null)
            throw new NullPointerException("name is null");
        this.name = name;
    }
    
}
