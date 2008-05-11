package com.mkalugin.pikachu.core.ast;

public interface ADocumentLevelVisitor {
    
    public void visitTextLine(ATextLine line);
    
    public void visitTaskLine(ATaskLine line);
    
    public void visitProjectLine(AProjectLine line);
    
    public void visitEmptyLine(AEmptyLine line);
    
}
