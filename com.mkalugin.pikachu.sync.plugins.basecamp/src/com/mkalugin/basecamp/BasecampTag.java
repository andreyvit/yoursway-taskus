package com.mkalugin.basecamp;

import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTag;

final class BasecampTag implements SynchronizableTag {
    
    private final String name;
    private final String value;

    public BasecampTag(String name, String value) {
        if (name == null)
            throw new NullPointerException("name is null");
        this.name = name;
        this.value = value;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean nameEquals(String tagName) {
        return name.equals(tagName);
    }
    
    public boolean valueEquals(SynchronizableTag another) {
        if (!another.getName().equals(name))
            throw new IllegalArgumentException("Must be comparing with the same kind of tag");
        String peerValue = another.getValue();
        return value == null && peerValue == null || value != null && value.equals(peerValue);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BasecampTag other = (BasecampTag) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

    public String getValue() {
        return value;
    }
}