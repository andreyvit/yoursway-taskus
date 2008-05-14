package com.mkalugin.pikachu.core.model;

public interface ApplicationModelListener {
    
    void documentOpened(Document document);
    
    void documentClosed(Document document);

    void documentFileChanged(Document document);
    
}
