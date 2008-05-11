package com.mkalugin.pikachu.core.model;

public class MTag {
    
    private String name;
    
    private String value;
    
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
    
}
