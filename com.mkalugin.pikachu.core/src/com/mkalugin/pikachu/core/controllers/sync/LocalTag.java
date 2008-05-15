package com.mkalugin.pikachu.core.controllers.sync;

import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTag;
import com.mkalugin.pikachu.core.model.document.structure.MTag;

public class LocalTag implements SynchronizableTag {
    
    private final String name;
    private final String value;
    
    public LocalTag(MTag tag) {
        if (tag == null)
            throw new NullPointerException("name is null");
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
    
    public boolean valueEquals(SynchronizableTag another) {
        LocalTag peer = (LocalTag) another;
        if (!peer.getName().equals(name))
            throw new IllegalArgumentException("Must be comparing with the same kind of tag");
        return value == null && peer.value == null || value != null && value.equals(peer.value);
    }
    
    @Override
    public String toString() {
        if (value != null)
            return "@" + name + "(" + value + ")";
        else
            return "@" + name;
    }
    
}
