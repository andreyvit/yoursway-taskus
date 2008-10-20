package com.mkalugin.corchy.internal.ui.editor;

import org.eclipse.swt.graphics.TextStyle;

public interface DocumentStylesheet {
    
    void styleGroup(TextStyle style);
    
    void styleText(TextStyle style);
    
    void styleTaskLeader(TextStyle style);
    
    void styleTask(TextStyle style);
    
    void styleDoneTask(TextStyle style);
    
    void styleTag(TextStyle style);
    
    void dispose();
    
}
