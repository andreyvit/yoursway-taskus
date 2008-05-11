package com.mkalugin.pikachu.core.model;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;

public class MTask extends MElement {
    
    private String name;
    
    private Collection<MTag> tags = newArrayList();
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void addTag(MTag tag) {
        if (tag == null)
            throw new NullPointerException("tag is null");
        tags.add(tag);
    }
    
    public void removeTag(MTag tag) {
        if (tag == null)
            throw new NullPointerException("tag is null");
        tags.remove(tag);
    }

    @Override
    public String toString() {
        return "Task " + name;
    }
    
}
