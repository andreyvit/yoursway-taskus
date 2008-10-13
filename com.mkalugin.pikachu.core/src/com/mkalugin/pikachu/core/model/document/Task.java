package com.mkalugin.pikachu.core.model.document;

public class Task extends NamedContainer {
    
    public Task(int start, int end) {
        super(start, end);
    }
    
    @Override
    protected boolean doesChildMatch(Element child) {
        return child instanceof Task || child instanceof Text;
    }
    
}
