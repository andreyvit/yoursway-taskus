package com.mkalugin.pikachu.core.ast;

import java.util.List;

import com.mkalugin.pikachu.core.workspace.TestingUtils;

public abstract class ANodeImpl implements ANode {
    
    private final int start;
    private final int end;
    
    public ANodeImpl(int start, int end) {
        this.start = start;
        this.end = end;
    }
    
    @Override
    public abstract String toString();
    
    protected String containerToString(List<?> children) {
        return TestingUtils.containerToString(getClass().getSimpleName(), children);
    }
    
}
