package com.mkalugin.pikachu.core.controllers;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import com.mkalugin.pikachu.core.controllers.viewglue.ApplicationPresentation;
import com.mkalugin.pikachu.core.controllers.viewglue.ApplicationPresentationCallback;
import com.mkalugin.pikachu.core.controllers.viewglue.ApplicationPresentationFactory;
import com.mkalugin.pikachu.core.model.ApplicationModel;
import com.mkalugin.pikachu.core.model.Document;

public class ApplicationController implements ApplicationPresentationCallback {
    
    private ApplicationPresentation applicationPresentation;
    private final ApplicationModel model;
    
    public ApplicationController(ApplicationModel model, ApplicationPresentationFactory presentationFactory) {
        if (model == null)
            throw new NullPointerException("model is null");
        this.model = model;
        applicationPresentation = presentationFactory.createPresentation(this);
    }
    
    public void run() {
        Collection<Document> documents = model.openAllUntitledDocuments();
        if (documents.isEmpty())
            openNewDocument();
        else
            for (Document document : documents)
                open(document);
        applicationPresentation.run();
    }
    
    public void openNewDocument() {
        try {
            Document document = model.createEmptyDocument();
            open(document);
        } catch (IOException e) {
            applicationPresentation.displayFailedToCreateEmptyDocumentError();
        }
    }
    
    public void openDocument() {
        File file = applicationPresentation.chooseDocumentToOpen(model.documentTypeDefinition());
        if (file != null) {
            try {
                Document document = model.openDocument(file);
                open(document);
            } catch (IOException e) {
                applicationPresentation.displayFailedToOpenError(file);
            }
        }
    }

    private void open(Document document) {
        DocumentWindowController controller = new DocumentWindowController(document,
                applicationPresentation);
        controller.openDocumentWindow();
    }
    
}
