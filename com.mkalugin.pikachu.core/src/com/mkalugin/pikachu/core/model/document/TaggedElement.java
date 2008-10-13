package com.mkalugin.pikachu.core.model.document;

import static com.google.common.collect.Lists.newArrayList;
import static com.mkalugin.pikachu.core.workspace.TestingUtils.containerToString;

import java.util.Collection;

public abstract class TaggedElement extends SimpleElement implements Tagged {
    
    private final Collection<Tag> tags = newArrayList();
    
    public TaggedElement(int start, int end) {
        super(start, end);
    }
    
    public Collection<Tag> getTags() {
        return tags;
    }
    
    public void addTag(Tag tag) {
        tags.add(tag);
        changed();
    }
    
    public void removeTag(Tag tag) {
        tags.remove(tag);
        changed();
    }
    
    @Override
    public String toString() {
        return super.toString() + containerToString("tags", newArrayList(tags));
    }
    
}
