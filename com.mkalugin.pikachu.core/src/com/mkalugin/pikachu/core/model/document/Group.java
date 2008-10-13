package com.mkalugin.pikachu.core.model.document;

public class Group extends NamedContainer {
    
    public Group(int start, int end) {
        super(start, end);
    }
    
    @Override
    protected boolean doesChildMatch(Element child) {
        return !(child instanceof Chapter || child instanceof Section);
    }
    
}
