package com.mkalugin.pikachu.core.model.document;

public class Token extends SimpleElement {
    
    private String text;
    
    public Token(int start, int end) {
        super(start, end);
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String newText) {
        text = newText;
        changed();
    }
    
    @Override
    public String toString() {
        return super.toString() + " " + text;
    }
    
}
