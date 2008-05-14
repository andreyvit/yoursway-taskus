package com.mkalugin.pikachu.core.controllers.viewglue;

import java.io.File;

import com.mkalugin.pikachu.core.model.DocumentTypeDefinition;

public interface ApplicationPresentation extends DocumentWindowFactory {
    
    void run();

    File chooseDocumentToOpen(DocumentTypeDefinition documentTypeDefinition);

    void displayFailedToOpenError(File file);

    void displayFailedToCreateEmptyDocumentError();
    
}
