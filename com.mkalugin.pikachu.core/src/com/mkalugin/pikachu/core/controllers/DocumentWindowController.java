package com.mkalugin.pikachu.core.controllers;

import java.io.File;
import java.io.IOException;

import com.mkalugin.pikachu.core.DocumentListener;
import com.mkalugin.pikachu.core.ast.AProjectName;
import com.mkalugin.pikachu.core.controllers.search.SearchController;
import com.mkalugin.pikachu.core.controllers.viewglue.DocumentWindow;
import com.mkalugin.pikachu.core.controllers.viewglue.DocumentWindowCallback;
import com.mkalugin.pikachu.core.controllers.viewglue.DocumentWindowFactory;
import com.mkalugin.pikachu.core.controllers.viewglue.FileNameRequestor;
import com.mkalugin.pikachu.core.controllers.viewglue.SaveDiscardCancel;
import com.mkalugin.pikachu.core.model.Document;

public class DocumentWindowController implements DocumentWindowCallback, DocumentListener {
    
    private final DocumentWindow window;
    
    private final Document document;
    
    private final DocumentSavingAgent savingAgent;
    
    private final Selection selection;
    
    private final SynchronizationCallback synchCallback;
    
    public DocumentWindowController(Document document, DocumentWindowFactory factory) {
        if (document == null)
            throw new NullPointerException("model is null");
        this.document = document;
        this.window = factory.createDocumentWindow(this);
        this.selection = new Selection();
        setBindingToWindow();
        document.addListener(this);
        new OutlineViewController(document, selection, window);
        new SourceViewController(document, selection, window, this);
        new SearchController(document, window);
        savingAgent = new DocumentSavingAgent(document);
        synchCallback = new SynchronizationCallback(window);
    }
    
    public void startSynchronization(final AProjectName projectName) {
        window.openSynchProgressSheet();
        new Thread(new Runnable() {
            
            public void run() {
                try {
                    
                    SynchronizationController controller = new SynchronizationController(document,
                            synchCallback);
                    controller.run(window, projectName);
                    
                } finally {
                    window.closeSynchProgressSheet();
                }
            }
            
        }).start();
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
                        doSaveAs(true);
                    }
                    
                });
            return false;
        } else {
            document.close();
        }
        return true;
    }
    
    public void saveFileAs() {
        doSaveAs(false);
    }
    
    void doSaveAs(final boolean closeWhenDone) {
        window.chooseFileNameToSaveInto(document.getBinding(), document.documentTypeDefinition(),
                new FileNameRequestor() {
                    
                    public void cancelled() {
                    }
                    
                    public void fileSelected(File file) {
                        try {
                            document.saveAs(file);
                            if (closeWhenDone)
                                document.close();
                        } catch (IOException e) {
                            window.reportSavingFailed(file);
                        }
                    }
                    
                });
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
