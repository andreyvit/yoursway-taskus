package com.mkalugin.pikachu.core.model.document;

import static com.google.common.collect.Lists.newArrayList;
import static com.mkalugin.pikachu.core.workspace.TestingUtils.containerToString;
import static java.util.Collections.unmodifiableList;

import java.util.Collection;
import java.util.List;

public abstract class TaggedContainer extends SimpleElement implements Tagged, Container {
    
    private final List<Element> children = newArrayList();
    
    private final Collection<Tag> tags = newArrayList();
    
    public TaggedContainer(int start, int end) {
        super(start, end);
    }
    
    public void addChild(Element child) {
        if (child == null)
            throw new NullPointerException("child is null");
        if (!doesChildMatch(child))
            throw new IllegalArgumentException("child doesn't match");
        
        children.add(child);
        changed();
    }
    
    public void removeChild(Element child) {
        if (child == null)
            throw new NullPointerException("child is null");
        
        children.remove(child);
        changed();
    }
    
    public List<Element> getChildren() {
        return unmodifiableList(children);
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
        String description = containerDescription() + tagsDescription();
        return containerToString(description, children);
    }
    
    protected String containerDescription() {
        return super.toString();
    }
    
    private String tagsDescription() {
        StringBuilder sb = new StringBuilder();
        for (Tag tag : tags) {
            sb.append(" @" + tag.getName());
            if (tag.isValueSet())
                sb.append("=" + tag.getValue());
        }
        return sb.toString();
    }
    
    public void correctEndOffset() {
        if (children.isEmpty())
            return;
        
        Element lastChild = children.get(children.size() - 1);
        int lastChildEnd = lastChild.range().end();
        if (range.end() < lastChildEnd)
            range = new Range(range.start(), lastChildEnd);
    }
}
