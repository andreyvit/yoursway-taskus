package com.mkalugin.pikachu.core.model.document.structure;

import com.mkalugin.pikachu.core.ast.ARange;

public class MTag {
    
    private String name;
    
    private String value;
    
    private ARange range;
    
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

    public void setRange(ARange range) {
        this.range = range;
    }

    public ARange getRange() {
        return range;
    }
    
}
