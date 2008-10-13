package com.mkalugin.pikachu.core.model.document;

public class NamedContainer extends TaggedContainer implements Named {
    
    Token name;
    
    public NamedContainer(int start, int end) {
        super(start, end);
    }
    
    public String getName() {
        return name.getText();
    }
    
    public Token getNameToken() {
        return name;
    }
    
    public void setName(String newName) {
        name.setText(newName);
    }
    
    @Override
    protected String containerDescription() {
        return super.containerDescription() + " " + name;
    }
    
}
