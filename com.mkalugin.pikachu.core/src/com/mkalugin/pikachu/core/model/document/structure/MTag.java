package com.mkalugin.pikachu.core.model.document.structure;

import com.mkalugin.pikachu.core.ast.ATag;

public class MTag {
    
    private String name;
    
    private String value;
    
    private ATag node;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public boolean isValueSet() {
        return value != null;
    }

    public void setNode(ATag node) {
        this.node = node;
    }

    public ATag getNode() {
        return node;
    }
    
}
