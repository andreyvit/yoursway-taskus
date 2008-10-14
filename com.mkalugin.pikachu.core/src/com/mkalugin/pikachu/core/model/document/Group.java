package com.mkalugin.pikachu.core.model.document;

public class Group extends NamedContainer {
    
    public Group(Token name, int start, int end) {
        super(name, start, end);
    }
    
    public boolean doesChildMatch(Element child) {
        return !(child instanceof Chapter || child instanceof Section);
    }
    
}
