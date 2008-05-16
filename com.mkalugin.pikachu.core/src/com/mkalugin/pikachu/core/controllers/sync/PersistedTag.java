package com.mkalugin.pikachu.core.controllers.sync;

import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTag;
import com.mkalugin.pikachu.core.model.document.structure.MTag;

public class PersistedTag implements SynchronizableTag {
    
    private final String name;
    private final String value;
    private final MTag tag;
    
    public PersistedTag(MTag tag) {
        if (tag == null)
            throw new NullPointerException("name is null");
        this.tag = tag;
        this.name = tag.getName();
        this.value = tag.getValue();
    }
    
    public boolean nameEquals(String tagName) {
        return name.equals(tagName);
    }
    
    public String getName() {
        return name;
    }
    
    public String getValue() {
        return value;
    }
    
    public MTag getTag() {
        return tag;
    }
    
    public boolean valueEquals(SynchronizableTag another) {
        if (!another.getName().equals(name))
            throw new IllegalArgumentException("Must be comparing with the same kind of tag");
        String peerValue = another.getValue();
        return value == null && peerValue == null || value != null && value.equals(peerValue);
    }
    
    @Override
    public String toString() {
        if (value != null)
            return "@" + name + "(" + value + ")";
        else
            return "@" + name;
    }
    
}
