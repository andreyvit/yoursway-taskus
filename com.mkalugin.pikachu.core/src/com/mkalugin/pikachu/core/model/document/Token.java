package com.mkalugin.pikachu.core.model.document;

public class Token extends SimpleElement {
    
    private final String text;
    
    public Token(String text, int start, int end) {
        super(start, end);
        
        if (text == null)
            throw new NullPointerException("text is null");
        
        this.text = text;
    }
    
    public String getText() {
        return text;
    }
    
    @Override
    public String toString() {
        return super.toString() + " " + text;
    }
    
    public void accept(DocumentModelVisitor visitor) {
        // not visit it
    }
    
}
