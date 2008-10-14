package com.mkalugin.pikachu.core.model.document;

public class Task extends NamedContainer {
    
    public Task(Token name, int start, int end) {
        super(name, start, end);
    }
    
    public boolean doesChildMatch(Element child) {
        return child instanceof Task || child instanceof Text;
    }
    
}
