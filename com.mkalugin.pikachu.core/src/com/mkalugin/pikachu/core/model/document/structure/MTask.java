package com.mkalugin.pikachu.core.model.document.structure;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;

import com.mkalugin.pikachu.core.ast.ATaskLine;

public class MTask extends MElement {
    
    private String name;
    
    private Collection<MTag> tags = newArrayList();
    
    private ATaskLine node;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void addTag(MTag tag) {
        if (tag == null)
            throw new NullPointerException("tag is null");
        tags.add(tag);
    }
    
    public void removeTag(MTag tag) {
        if (tag == null)
            throw new NullPointerException("tag is null");
        tags.remove(tag);
    }
    
    public Collection<MTag> getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return "Task " + name;
    }

    public void setNode(ATaskLine node) {
        this.node = node;
    }

    public ATaskLine getNode() {
        return node;
    }
    
}
