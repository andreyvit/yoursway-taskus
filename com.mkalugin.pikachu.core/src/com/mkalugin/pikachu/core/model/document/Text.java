package com.mkalugin.pikachu.core.model.document;

public class Text extends TaggedElement {
    
    private String text;
    
    public Text(int start, int end) {
        super(start, end);
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String newText) {
        text = newText;
        changed();
    }
    
}
