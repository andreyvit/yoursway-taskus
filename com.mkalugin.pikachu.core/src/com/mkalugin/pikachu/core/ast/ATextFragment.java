package com.mkalugin.pikachu.core.ast;

public abstract class ATextFragment extends ANodeImpl {

    private final String text;

    public ATextFragment(int start, int end, String text) {
        super(start, end);
        this.text = text;
    }
    
    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + text;
    }
    
}
