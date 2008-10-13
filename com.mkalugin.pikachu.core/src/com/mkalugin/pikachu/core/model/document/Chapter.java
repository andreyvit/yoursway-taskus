package com.mkalugin.pikachu.core.model.document;

public class Chapter extends NamedContainer {
    
    public Chapter(int start, int end) {
        super(start, end);
    }
    
    @Override
    protected boolean doesChildMatch(Element child) {
        return !(child instanceof Chapter);
    }
    
}
