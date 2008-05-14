package com.mkalugin.pikachu.core.model;

/**
 * This is a definition that needs to be communicated to the file choosing
 * code so that it can differentiate the application's files.
 */
public class DocumentTypeDefinition {
    
    private final String defaultDocumentExtension;

    public DocumentTypeDefinition(String defaultDocumentExtension) {
        if (defaultDocumentExtension == null)
            throw new NullPointerException("defaultDocumentExtension is null");
        this.defaultDocumentExtension = defaultDocumentExtension;
    }
    
    public String defaultExtension() {
        return defaultDocumentExtension;
    }

}
