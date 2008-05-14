package com.mkalugin.pikachu.core;

import com.mkalugin.pikachu.core.ast.ADocument;

public interface ICorchyWindow {
    
    void setText(String text);
    
    void highlightUsing(ADocument document);

    void openWindow();

    void setCallback(ViewCallback callback);
    
}
