package com.mkalugin.pikachu.core.model.document;

public abstract class NamedContainer extends TaggedContainer implements Named {
    
    Token name;
    
    public NamedContainer(Token name, int start, int end) {
        super(start, end);
        
        if (name == null)
            throw new NullPointerException("name is null");
        
        this.name = name;
    }
    
    public String getName() {
        return name.getText();
    }
    
    public Token getNameToken() {
        return name;
    }
    
    @Override
    protected String containerDescription() {
        return super.containerDescription() + " [" + name + "]";
    }
    
}
