package com.mkalugin.pikachu.core.model.document;

import static com.google.common.collect.Lists.newArrayList;
import static com.mkalugin.pikachu.core.workspace.TestingUtils.containerToString;
import static java.util.Collections.unmodifiableList;

import java.util.List;

public abstract class TaggedContainer extends TaggedElement implements Container {
    
    List<Element> children = newArrayList();
    
    public TaggedContainer(int start, int end) {
        super(start, end);
    }
    
    public void addChild(Element child) {
        if (child == null)
            throw new NullPointerException("child is null");
        if (!doesChildMatch(child))
            
            children.add(child);
        changed();
    }
    
    protected abstract boolean doesChildMatch(Element child);
    
    public void removeChild(Element child) {
        if (child == null)
            throw new NullPointerException("child is null");
        
        children.remove(child);
        changed();
    }
    
    public List<Element> getChildren() {
        return unmodifiableList(children);
    }
    
    @Override
    public String toString() {
        return containerToString(containerDescription(), children);
    }
    
    protected String containerDescription() {
        return super.toString();
    }
    
}
