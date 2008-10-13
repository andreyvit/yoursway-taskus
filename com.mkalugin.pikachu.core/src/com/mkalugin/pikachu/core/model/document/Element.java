package com.mkalugin.pikachu.core.model.document;

import com.yoursway.utils.EventSource;

public interface Element {
    
    public String toString();
    
    public Range range();
    
    public EventSource<DocumentModelListener> events();
    
}