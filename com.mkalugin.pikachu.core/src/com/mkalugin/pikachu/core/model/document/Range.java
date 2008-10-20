package com.mkalugin.pikachu.core.model.document;

public class Range {
    
    private final int start;
    private final int end;
    
    public Range(int start, int end) {
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
    
    @Override
    public String toString() {
        return "(" + start + ".." + end + ")";
    }
    
    public boolean contains(int start, int end) {
        return this.start <= start && end <= this.end;
    }
    
    public boolean contains(Range range) {
        return contains(range.start, range.end);
    }
    
}
