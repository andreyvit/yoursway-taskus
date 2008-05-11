package com.mkalugin.pikachu.core.ast;

public interface ATaskLevelNode extends ANode {
    
    void accept(ATaskLevelVisitor visitor);
    
}
