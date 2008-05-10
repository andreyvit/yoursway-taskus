package com.mkalugin.pikachu.core.ast;

public class ATextLine extends ANode {
    
    private final String data;

    public ATextLine(int start, int end, String data) {
        super(start, end);
        if (data == null)
            throw new NullPointerException("data is null");
        this.data = data;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + data;
    }

    public static ANode extract(int start, int end, CharSequence source) {
        return new ATextLine(start, end, source.subSequence(start, end).toString());
    }
    
}
