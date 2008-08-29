package com.mkalugin.pikachu.core.controllers.viewglue;

import java.io.File;

import com.mkalugin.pikachu.core.model.DocumentTypeDefinition;
import com.yoursway.autoupdater.auxiliary.AutoupdaterException;
import com.yoursway.autoupdater.auxiliary.Suite;
import com.yoursway.autoupdater.localrepository.LocalRepository;

public interface ApplicationPresentation extends DocumentWindowFactory {
    
    void run();
    
    File chooseDocumentToOpen(DocumentTypeDefinition documentTypeDefinition);
    
    void displayFailedToOpenError(File file);
    
    void displayFailedToCreateEmptyDocumentError();
    
    void openUpdater(Suite suite, LocalRepository localRepository);
    
    void displayFailedToUpdate(AutoupdaterException e);
    
}
