package com.mkalugin.pikachu.core.model.document;

public class Section extends NamedContainer {
    
    public Section(int start, int end) {
        super(start, end);
    }
    
    @Override
    protected boolean doesChildMatch(Element child) {
        return !(child instanceof Chapter);
    }
    
}
