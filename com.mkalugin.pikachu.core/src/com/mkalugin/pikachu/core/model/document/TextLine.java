package com.mkalugin.pikachu.core.model.document;

public class TextLine extends SimpleElement {
    
    private final String text;
    
    public TextLine(String text, int start, int end) {
        super(start, end);
        
        if (text == null)
            throw new NullPointerException("text is null");
        
        this.text = text;
    }
    
    public String getText() {
        return text;
    }
    
}
