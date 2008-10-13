package com.mkalugin.pikachu.core.model.document;

import java.util.Collection;

public interface Tagged {
    
    public Collection<Tag> getTags();
    
    public void addTag(Tag tag);
    
    public void removeTag(Tag tag);
    
}