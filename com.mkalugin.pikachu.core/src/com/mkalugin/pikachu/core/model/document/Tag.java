package com.mkalugin.pikachu.core.model.document;

import com.yoursway.utils.annotations.Nullable;

public class Tag extends SimpleElement implements Named {
    
    public Tag(int start, int end) {
        super(start, end);
    }
    
    private Token name;
    
    @Nullable
    private Token value;
    
    public String getName() {
        return name.getText();
    }
    
    public Token getNameToken() {
        return name;
    }
    
    public void setName(String newName) {
        name.setText(newName);
    }
    
    public String getValue() {
        return value.getText();
    }
    
    public Token getValueToken() {
        return name;
    }
    
    public void setValueToken(@Nullable Token newValueToken) {
        value = newValueToken;
        changed();
    }
    
    public boolean isValueSet() {
        return value != null;
    }
    
    @Override
    public String toString() {
        String s = super.toString() + " " + name;
        if (isValueSet())
            s += "=" + value;
        return s;
    }
    
}
