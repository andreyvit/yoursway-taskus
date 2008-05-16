package com.mkalugin.pikachu.core.controllers.viewglue;

public interface SourceViewCallback {

    void setText(String text);
    
    void selectionChanged(int start, int end);
    
}
