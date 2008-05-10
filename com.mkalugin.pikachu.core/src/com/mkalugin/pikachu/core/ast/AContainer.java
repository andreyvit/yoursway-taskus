package com.mkalugin.pikachu.core.ast;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

public abstract class AContainer extends ANode {
    
    private List<ANode> children = newArrayList();
    
    public AContainer(int start, int end) {
        super(start, end);
    }
    
    public void addChild(ANode node) {
        if (node == null)
            throw new NullPointerException("node is null");
        children.add(node);
    }
    
    @Override
    public final String toString() {
        return containerToString(children);
    }
    
}
