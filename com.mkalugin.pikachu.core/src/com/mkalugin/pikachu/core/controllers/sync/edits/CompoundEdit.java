package com.mkalugin.pikachu.core.controllers.sync.edits;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collections;
import java.util.List;

public class CompoundEdit extends Edit {
    
    private final List<Edit> children = newArrayList();
    
    private int fixup = 0;
    
    public void add(Edit edit) {
        children.add(edit);
    }
    
    @Override
    void fixup(int offset) {
        this.fixup += offset;
    }
    
    @Override
    public int apply(StringBuilder builder) {
        int commonFixup = this.fixup;
        int subseqFixup = 0;
        Collections.sort(children);
        for (Edit edit : children) {
            edit.fixup(commonFixup + subseqFixup);
            subseqFixup += edit.apply(builder);
        }
        return subseqFixup;
    }
    
    @Override
    int offset() {
        if (children.isEmpty())
            return -1;
        return children.iterator().next().offset();
    }
    
}
