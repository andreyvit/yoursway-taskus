package com.mkalugin.pikachu.core.model.document;

import java.util.Collection;

public interface Tagged {
    
    public Collection<Tag> getTags();
    
    public boolean hasTag(String name);
    
    public void addTag(Tag tag);
    
    public void removeTag(Tag tag);
    
}