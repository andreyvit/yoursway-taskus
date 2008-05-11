package com.mkalugin.pikachu.core.ast;

public class ATaskDescriptionFragment extends ATextFragment implements ATaskLevelNode {

    public ATaskDescriptionFragment(int start, int end, String name) {
        super(start, end, name);
    }

    public void accept(ATaskLevelVisitor visitor) {
        visitor.visitDescriptionFragment(this);
    }
    
}
