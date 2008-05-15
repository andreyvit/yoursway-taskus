package com.mkalugin.pikachu.core.controllers.viewglue;

import com.mkalugin.pikachu.core.ast.ADocument;

public interface SourceView {

    void setText(String content);

    void highlightAccordingTo(ADocument documentNode);
    
}
