package com.mkalugin.pikachu.core.ast;

public interface ADocumentLevelNode extends ANode {
    
    void accept(ADocumentLevelVisitor visitor);
    
}
