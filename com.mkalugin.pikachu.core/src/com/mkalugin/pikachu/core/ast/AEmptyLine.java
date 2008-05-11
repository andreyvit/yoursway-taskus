package com.mkalugin.pikachu.core.ast;

public class AEmptyLine extends ANodeImpl implements ADocumentLevelNode {
    
    public AEmptyLine(int start, int end) {
        super(start, end);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    public void accept(ADocumentLevelVisitor visitor) {
        visitor.visitEmptyLine(this);
    }
    
}
