package com.mkalugin.basecamp.model;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;

public class ToDoList {

    private final int id;
    private final String description;
    private final Collection<ToDoItem> items;
    private final String name;

    public ToDoList(int id, String name, String description, Collection<ToDoItem> items) {
        if (name == null)
            throw new NullPointerException("name is null");
        if (description == null)
            throw new NullPointerException("description is null");
        this.id = id;
        this.name = name;
        this.description = description;
        this.items = (items == null ? null : newArrayList(items));
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public Collection<ToDoItem> getItems() {
        if (items == null)
            throw new IllegalStateException("Items are not known for this todo-list");
        return items;
    }
    
}
