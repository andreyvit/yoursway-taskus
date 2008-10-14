package com.mkalugin.pikachu.core.model.document;

public class Text extends TaggedContainer {
    
    public Text(int start, int end) {
        super(start, end);
    }
    
    public boolean doesChildMatch(Element child) {
        return child instanceof TextLine;
    }
    
}
