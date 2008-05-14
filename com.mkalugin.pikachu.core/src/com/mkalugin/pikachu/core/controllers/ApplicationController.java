package com.mkalugin.pikachu.core.controllers;

import com.mkalugin.pikachu.core.Document;
import com.mkalugin.pikachu.core.controllers.viewglue.ApplicationPresentation;
import com.mkalugin.pikachu.core.controllers.viewglue.ApplicationPresentationCallback;
import com.mkalugin.pikachu.core.controllers.viewglue.ApplicationPresentationFactory;
import com.mkalugin.pikachu.core.model.ApplicationModel;

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
        openNewDocument();
        applicationPresentation.run();
    }

    public void openNewDocument() {
        Document document = model.createEmptyDocument();
        DocumentWindowController controller = new DocumentWindowController(document, applicationPresentation);
        controller.openDocumentWindow();
    }
    
}
