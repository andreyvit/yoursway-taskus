package com.kalugin.plugins.sync.api.tests.synchronizer.mocks;

import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTag;

public class SynchronizableTagImpl implements SynchronizableTag {
    
    private final String name;
    private final String value;
    
    public SynchronizableTagImpl(String name, String value) {
        if (name == null)
            throw new NullPointerException("name is null");
        this.name = name;
        this.value = value;
    }
    
    public boolean nameEquals(String tagName) {
        return name.equals(tagName);
    }
    
    public String getName() {
        return name;
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

    public String getValue() {
        return value;
    }
    
}
