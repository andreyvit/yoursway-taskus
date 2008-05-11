package com.mkalugin.pikachu.core.ast;

public class ATaskLeader extends ANodeImpl implements ATaskLevelNode {

    public ATaskLeader(int start, int end) {
        super(start, end);
    }

    @Override
    public String toString() {
        return "Leader";
    }

    public void accept(ATaskLevelVisitor visitor) {
        visitor.visitLeader(this);
    }
    
}
