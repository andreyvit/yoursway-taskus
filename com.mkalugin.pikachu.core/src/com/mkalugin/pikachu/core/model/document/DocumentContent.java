package com.mkalugin.pikachu.core.model.document;

public class DocumentContent extends TaggedContainer {
    
    public DocumentContent(int start, int end) {
        super(start, end);
    }
    
    @Override
    protected boolean doesChildMatch(Element child) {
        return true;
    }
    
}
