package com.mkalugin.pikachu.core.controllers.viewglue;

import java.io.File;

import com.mkalugin.pikachu.core.model.DocumentTypeDefinition;
import com.yoursway.autoupdater.auxiliary.UpdatableApplicationView;

public interface ApplicationPresentation extends DocumentWindowFactory, UpdatableApplicationView {
    
    void run();
    
    File chooseDocumentToOpen(DocumentTypeDefinition documentTypeDefinition);
    
    void displayFailedToOpenError(File file);
    
    void displayFailedToCreateEmptyDocumentError();
    
}
