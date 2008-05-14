package com.mkalugin.pikachu.core.controllers;

import java.io.File;
import java.io.IOException;

import com.mkalugin.pikachu.core.DocumentListener;
import com.mkalugin.pikachu.core.controllers.viewglue.DocumentWindow;
import com.mkalugin.pikachu.core.controllers.viewglue.DocumentWindowCallback;
import com.mkalugin.pikachu.core.controllers.viewglue.DocumentWindowFactory;
import com.mkalugin.pikachu.core.controllers.viewglue.SaveDiscardCancel;
import com.mkalugin.pikachu.core.model.Document;

public class DocumentWindowController implements DocumentWindowCallback, DocumentListener {
    
    private final DocumentWindow window;
    
    private final Document document;
    
    private DocumentSavingAgent savingAgent;
    
    public DocumentWindowController(Document document, DocumentWindowFactory factory) {
        if (document == null)
            throw new NullPointerException("model is null");
        this.document = document;
        this.window = factory.createDocumentWindow(this);
        setBindingToWindow();
        document.addListener(this);
        new OutlineViewController(document, window);
        new SourceViewController(document, window);
        savingAgent = new DocumentSavingAgent(document);
    }
    
    public void startSynchronization() {
    }
    
    public void openDocumentWindow() {
        window.setText(document.getContent());
        window.openWindow();
    }
    
    public boolean closeFile() {
        if (document.isUntitled()) {
            if (document.isEmpty())
                document.discard();
            else
                window.askSaveDiscardCancel(new SaveDiscardCancel() {
                    
                    public void cancel() {
                    }
                    
                    public void discard() {
                        document.discard();
                    }
                    
                    public void save() {
                        if (saveFileAs())
                            document.close();
                    }
                    
                });
            return false;
        } else {
            document.close();
        }
        return true;
    }
    
    public boolean saveFileAs() {
        File file = window.chooseFileNameToSaveInto(document.getBinding(), document.documentTypeDefinition());
        if (file != null)
            try {
                document.saveAs(file);
                return true;
            } catch (IOException e) {
                window.reportSavingFailed(file);
            }
        return false;
    }
    
    public void bindingChanged() {
        setBindingToWindow();
    }
    
    public void contentChanged(Object sender) {
    }
    
    public void closed(boolean discarded) {
        savingAgent.dispose();
        window.close();
    }
    
    public void emptinessChanged() {
        setBindingToWindow();
    }
    
    private void setBindingToWindow() {
        window.setDocumentBinding(document.getBinding(), document.isEmpty());
    }
    
}
