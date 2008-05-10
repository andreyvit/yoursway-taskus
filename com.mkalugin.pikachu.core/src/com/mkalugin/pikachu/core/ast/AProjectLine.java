package com.mkalugin.pikachu.core.ast;

public class AProjectLine extends ANode {

    private final AProjectName name;

    public AProjectLine(int start, int end, AProjectName name) {
        super(start, end);
        if (name == null)
            throw new NullPointerException("name is null");
        this.name = name;
    }

    @Override
    public String toString() {
        return name.toString();
    }
    
}
