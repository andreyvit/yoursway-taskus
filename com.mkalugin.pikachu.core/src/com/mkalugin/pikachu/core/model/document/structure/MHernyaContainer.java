package com.mkalugin.pikachu.core.model.document.structure;

import static com.google.common.collect.Lists.newArrayList;
import static com.mkalugin.pikachu.core.workspace.TestingUtils.containerToString;
import static java.util.Collections.unmodifiableList;

import java.util.Collections;
import java.util.List;

public abstract class MHernyaContainer extends MElement {
    
    private List<MElement> children = newArrayList();
    
    public void addChild(MElement child) {
        if (child == null)
            throw new NullPointerException("child is null");
        children.add(child);
    }
    
    public void removeChild(MElement child) {
        if (child == null)
            throw new NullPointerException("child is null");
        children.remove(child);
    }
    
    public List<MElement> getChildren() {
        return unmodifiableList(children);
    }
    
    @Override
    public String toString() {
        return containerToString(containerDescription(), children);
    }

    protected String containerDescription() {
        return getClass().getSimpleName();
    }
    
}
