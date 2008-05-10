package com.mkalugin.pikachu.core.ast;

public class AEmptyLine extends ANode {
    
    public AEmptyLine(int start, int end) {
        super(start, end);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
    
}
