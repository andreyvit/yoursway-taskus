package com.mkalugin.pikachu.core.model.document;

public class Directive extends SimpleElement {
    
    public Directive(int start, int end) {
        super(start, end);
    }
    
    private String commandText;
    
    public String getCommandText() {
        return commandText;
    }
    
    public void setCommandText(String newCommandText) {
        commandText = newCommandText;
        changed();
    }
    
}
