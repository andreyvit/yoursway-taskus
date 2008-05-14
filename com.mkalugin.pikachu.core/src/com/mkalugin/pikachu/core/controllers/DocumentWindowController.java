package com.mkalugin.pikachu.core.controllers;

import java.io.File;
import java.io.IOException;

import com.mkalugin.pikachu.core.DocumentListener;
import com.mkalugin.pikachu.core.controllers.viewglue.DocumentWindow;
import com.mkalugin.pikachu.core.controllers.viewglue.DocumentWindowCallback;
import com.mkalugin.pikachu.core.controllers.viewglue.DocumentWindowFactory;
import com.mkalugin.pikachu.core.controllers.viewglue.SaveDiscardCancel;
import com.mkalugin.pikachu.core.model.Document;

public class DocumentWindowController implements DocumentWindowCallback {
    
    private final DocumentWindow window;
    
    private final Document document;
    
    public DocumentWindowController(Document document, DocumentWindowFactory factory) {
        if (document == null)
            throw new NullPointerException("model is null");
        this.document = document;
        this.window = factory.createDocumentWindow(this);
        window.setDocumentBinding(document.getBinding());
        new OutlineViewController(document, window);
        new SourceViewController(document, window);
        new DocumentSavingAgent(document);
    }

    public void startSynchronization() {
    }
    
    static class DocumentSavingAgent implements DocumentListener {
        
        private final Document document;
        
        public DocumentSavingAgent(Document document) {
            this.document = document;
            document.addListener(this);
        }

        public void bindingChanged() {
        }

        public void contentChanged(Object sender) {
            try {
                document.save();
            } catch (IOException e) {
                // FIXME handle saving errors more gracefully
                e.printStackTrace(System.err);
            }
        }
        
    }

    public void openDocumentWindow() {
        window.setText(document.getContent());
        window.openWindow();
    }

    public boolean closeFile() {
        if (document.isUntitled()) {
            window.askSaveDiscardCancel(new SaveDiscardCancel() {

                public void cancel() {
                }

                public void discard() {
                    window.close();
                }

                public void save() {
                }
                
            });
            return false;
        }
        return true;
    }

    public void saveFileAs() {
        File file = window.chooseFileNameToSaveInto(document.getBinding(), document.documentTypeDefinition());
        if (file != null)
            try {
                document.saveAs(file);
            } catch (IOException e) {
                window.reportSavingFailed(file);
            }
    }
    
}
