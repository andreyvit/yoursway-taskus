package com.mkalugin.pikachu.core.controllers;

import java.io.File;
import java.io.IOException;

import com.mkalugin.pikachu.core.controllers.viewglue.ApplicationPresentation;
import com.mkalugin.pikachu.core.controllers.viewglue.ApplicationPresentationCallback;
import com.mkalugin.pikachu.core.controllers.viewglue.ApplicationPresentationFactory;
import com.mkalugin.pikachu.core.model.ApplicationModel;
import com.mkalugin.pikachu.core.model.ApplicationModelListener;
import com.mkalugin.pikachu.core.model.Document;
import com.mkalugin.pikachu.core.preference.IPreferenceStore;

public class ApplicationController implements ApplicationPresentationCallback, ApplicationModelListener {
    
    private final ApplicationPresentation applicationPresentation;
    private final ApplicationModel model;
    private final AutoupdaterController autoupdaterController;
    
    public ApplicationController(ApplicationModel model, IPreferenceStore preferences,
            ApplicationPresentationFactory presentationFactory) {
        if (model == null)
            throw new NullPointerException("model is null");
        this.model = model;
        model.addListener(this);
        applicationPresentation = presentationFactory.createPresentation(this);
        autoupdaterController = new AutoupdaterController(preferences, applicationPresentation);
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
        autoupdaterController.updateApplication();
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
