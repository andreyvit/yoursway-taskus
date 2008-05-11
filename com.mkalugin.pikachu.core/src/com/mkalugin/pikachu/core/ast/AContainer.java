package com.mkalugin.pikachu.core.ast;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.unmodifiableList;

import java.util.List;

public abstract class AContainer<ChildT extends ANode> extends ANodeImpl {
    
    private List<ChildT> children = newArrayList();
    
    public AContainer(int start, int end) {
        super(start, end);
    }
    
    public void addChild(ChildT node) {
        if (node == null)
            throw new NullPointerException("node is null");
        children.add(node);
    }
    
    public List<ChildT> getChildren() {
        return unmodifiableList(children);
    }
    
    @Override
    public final String toString() {
        return containerToString(children);
    }
    
}
