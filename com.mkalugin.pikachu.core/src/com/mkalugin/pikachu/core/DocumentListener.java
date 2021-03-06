package com.mkalugin.pikachu.core;

public interface DocumentListener {
    
    void contentChanged(Object sender);
    
    void bindingChanged();
    
    void emptinessChanged();

    void closed(boolean discarded);
    
}
