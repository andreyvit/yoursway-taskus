package com.mkalugin.pikachu.core.ast;

public class ATaskName extends ATextFragment implements ATaskLevelNode {

    public ATaskName(int start, int end, String name) {
        super(start, end, name);
    }

    public static ATaskName extract(int start, int end, CharSequence source) {
        return new ATaskName(start, end, source.subSequence(start, end).toString());
    }

    public void accept(ATaskLevelVisitor visitor) {
        visitor.visitName(this);
    }
    
}
