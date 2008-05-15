package com.mkalugin.pikachu.core.ast;

public class ARange {
    
    private final int start;
    private final int end;

    public ARange(int start, int end) {
        this.start = start;
        this.end = end;
    }
    
    public int start() {
        return start;
    }
    
    public int end() {
        return end;
    }
    
    public int length() {
        return end - start;
    }
    
}
