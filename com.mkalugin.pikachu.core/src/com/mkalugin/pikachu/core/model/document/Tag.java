package com.mkalugin.pikachu.core.model.document;

import com.yoursway.utils.annotations.Nullable;

public class Tag extends SimpleElement implements Named {
    
    private final Token name;
    
    @Nullable
    private Token value;
    
    public Tag(Token name, @Nullable Token value, int start, int end) {
        super(start, end);
        
        if (name == null)
            throw new NullPointerException("name is null");
        
        this.name = name;
        this.value = value;
    }
    
    public String getName() {
        return name.getText();
    }
    
    public Token getNameToken() {
        return name;
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
    
    public void accept(DocumentModelVisitor visitor) {
        visitor.visit(this);
    }
    
}
