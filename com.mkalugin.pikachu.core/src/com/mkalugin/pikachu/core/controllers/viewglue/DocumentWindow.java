package com.mkalugin.pikachu.core.controllers.viewglue;

import com.mkalugin.pikachu.core.ast.ADocument;

public interface DocumentWindow extends OutlineViewFactory, SourceViewFactory {
    
    void setText(String text);
    
    void highlightUsing(ADocument document);

    void openWindow();
    
}
