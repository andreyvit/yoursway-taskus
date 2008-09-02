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
import com.yoursway.autoupdater.auxiliary.SuiteDefinition;
import com.yoursway.autoupdater.auxiliary.UpdatableApplication;
import com.yoursway.autoupdater.localrepository.LocalRepository;
import com.yoursway.autoupdater.localrepository.LocalRepositoryException;

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
        final UpdatableApplication app = new UpdatableApplication() {
            public SuiteDefinition suite() {
                try {
                    // return SuiteDefinition.load("http://yoursway-updates.s3.amazonaws.com/", "taskus");
                    return SuiteDefinition.load("http://rus.yoursway.com:8888/updatesite/", "taskus");
                } catch (AutoupdaterException e) {
                    applicationPresentation.displayFailedToUpdate(e);
                    return null;
                }
            }
            
            public LocalRepository localRepository() {
                try {
                    return LocalRepository.createForGUI(this);
                } catch (LocalRepositoryException e) {
                    applicationPresentation.displayFailedToUpdate(e);
                    return null;
                }
            }
            
            public File rootFolder(String productName) throws IOException {
                String path = System.getProperty("user.dir");
                if (path.contains("Eclipse.app"))
                    throw new AssertionError("OOPS!");
                return new File(path); //!
            }
        };
        
        applicationPresentation.openUpdater(app);
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
