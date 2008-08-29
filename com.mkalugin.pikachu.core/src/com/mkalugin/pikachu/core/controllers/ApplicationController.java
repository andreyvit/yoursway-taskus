package com.mkalugin.pikachu.core.controllers;

import java.io.File;
import java.io.IOException;

import com.mkalugin.pikachu.core.controllers.viewglue.ApplicationPresentation;
import com.mkalugin.pikachu.core.controllers.viewglue.ApplicationPresentationCallback;
import com.mkalugin.pikachu.core.controllers.viewglue.ApplicationPresentationFactory;
import com.mkalugin.pikachu.core.model.ApplicationModel;
import com.mkalugin.pikachu.core.model.ApplicationModelListener;
import com.mkalugin.pikachu.core.model.Document;
import com.yoursway.autoupdater.auxiliary.AutoupdaterException;
import com.yoursway.autoupdater.auxiliary.Suite;
import com.yoursway.autoupdater.localrepository.LocalRepository;

public class ApplicationController implements ApplicationPresentationCallback, ApplicationModelListener {
    
    private final ApplicationPresentation applicationPresentation;
    private final ApplicationModel model;
    
    public ApplicationController(ApplicationModel model, ApplicationPresentationFactory presentationFactory) {
        if (model == null)
            throw new NullPointerException("model is null");
        this.model = model;
        model.addListener(this);
        applicationPresentation = presentationFactory.createPresentation(this);
    }
    
    public void run() {
        model.openAllUntitledDocuments();
        if (model.areNoDocumentsOpen())
            openNewDocument();
        applicationPresentation.run();
    }
    
    public void openNewDocument() {
        try {
            model.openNewDocument();
        } catch (IOException e) {
            applicationPresentation.displayFailedToCreateEmptyDocumentError();
        }
    }
    
    public void openDocument() {
        File file = applicationPresentation.chooseDocumentToOpen(model.documentTypeDefinition());
        if (file != null) {
            try {
                model.openExistingDocument(file);
            } catch (IOException e) {
                applicationPresentation.displayFailedToOpenError(file);
            }
        }
    }
    
    public void updateApplication() {
        try {
            Suite suite = Suite.load("http://yoursway-updates.s3.amazonaws.com/", "taskus");
            LocalRepository localRepository = LocalRepository.createForGUI();
            applicationPresentation.openUpdater(suite, localRepository);
        } catch (AutoupdaterException e) {
            applicationPresentation.displayFailedToUpdate(e);
        }
    }
    
    public void documentClosed(Document document) {
    }
    
    public void documentOpened(Document document) {
        DocumentWindowController controller = new DocumentWindowController(document, applicationPresentation);
        controller.openDocumentWindow();
    }
    
    public void documentFileChanged(Document document) {
    }
    
}
