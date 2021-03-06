package com.mkalugin.pikachu.core.model;

public interface DocumentOwner {
    
    DocumentTypeDefinition documentTypeDefinition();

    void documentClosed(Document document);

    void documentFileChanged(Document document);    
    
}
