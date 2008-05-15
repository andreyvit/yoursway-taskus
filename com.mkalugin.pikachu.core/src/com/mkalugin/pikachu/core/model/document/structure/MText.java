package com.mkalugin.pikachu.core.model.document.structure;

import com.mkalugin.pikachu.core.ast.ARange;
import com.mkalugin.pikachu.core.ast.ATextLine;

public class MText extends MElement {
    
    private final ATextLine textLine;

    public MText(ATextLine textLine) {
        this.textLine = textLine;
    }
    
    public String getText() {
        return textLine.getData();
    }
    
    @Override
    public String toString() {
        return "Text " + getText();
    }

    public ARange range() {
        return textLine.range();
    }
    
}
