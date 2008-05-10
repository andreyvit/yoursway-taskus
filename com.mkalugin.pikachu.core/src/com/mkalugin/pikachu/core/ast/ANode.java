package com.mkalugin.pikachu.core.ast;

import static com.google.common.base.Functions.TO_STRING;
import static com.google.common.base.Join.join;
import static com.google.common.collect.Iterables.transform;

import java.util.List;

import com.google.common.base.Function;

public abstract class ANode {
    
    private final int start;
    private final int end;
    
    public ANode(int start, int end) {
        this.start = start;
        this.end = end;
    }
    
    @Override
    public abstract String toString();
    
    protected final static Function<String, String> FIXUP_NEWLINES = new Function<String, String>() {
        
        public String apply(String s) {
            return s.trim().replaceAll("\n", "\n-");
        }
        
    };
    
    protected String containerToString(List<ANode> children) {
        String container = getClass().getSimpleName();
        if (children.isEmpty())
            return container + " (empty)\n";
        else
            return container + ":\n-"
                    + join("\n-", transform(transform(children, TO_STRING), FIXUP_NEWLINES)) + "\n";
    }
    
}
