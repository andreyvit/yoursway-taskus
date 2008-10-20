package com.mkalugin.pikachu.core.model.document;

public class Directive extends SimpleElement {
    
    private String commandText;
    
    public Directive(String command, int start, int end) {
        super(start, end);
        
        if (command == null)
            throw new NullPointerException("command is null");
        
        commandText = command;
    }
    
    public String getCommandText() {
        return commandText;
    }
    
    public void setCommandText(String newCommandText) {
        commandText = newCommandText;
        changed();
    }
    
    public void accept(DocumentModelVisitor visitor) {
        visitor.visit(this);
    }
    
}
