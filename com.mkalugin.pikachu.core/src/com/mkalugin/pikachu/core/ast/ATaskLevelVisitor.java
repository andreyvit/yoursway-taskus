package com.mkalugin.pikachu.core.ast;

public interface ATaskLevelVisitor {
    
    void visitLeader(ATaskLeader leader);
    
    void visitName(ATaskName name);
    
    void visitTag(ATag tag);

    void visitDescriptionFragment(ATaskDescriptionFragment fragment);
    
}
