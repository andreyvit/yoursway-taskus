package com.mkalugin.pikachu.core.controllers;

import com.mkalugin.pikachu.core.Document;
import com.mkalugin.pikachu.core.controllers.viewglue.DocumentWindow;
import com.mkalugin.pikachu.core.controllers.viewglue.DocumentWindowCallback;
import com.mkalugin.pikachu.core.controllers.viewglue.DocumentWindowFactory;
import com.mkalugin.pikachu.core.controllers.viewglue.OutlineView;
import com.mkalugin.pikachu.core.controllers.viewglue.OutlineViewCallback;
import com.mkalugin.pikachu.core.controllers.viewglue.OutlineViewFactory;
import com.mkalugin.pikachu.core.controllers.viewglue.SourceView;
import com.mkalugin.pikachu.core.controllers.viewglue.SourceViewCallback;
import com.mkalugin.pikachu.core.controllers.viewglue.SourceViewFactory;

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
    }

    public void startSynchronization() {
    }

    static class OutlineViewController implements OutlineViewCallback {
        
        private final OutlineView outlineView;

        public OutlineViewController(Document document, OutlineViewFactory factory) {
            outlineView = factory.bindOutlineView(this);
        }
        
    }
    
    static class SourceViewController implements SourceViewCallback {
        
        private SourceView sourceView;

        public SourceViewController(Document document, SourceViewFactory factory) {
            sourceView = factory.bindSourceView(this);
        }

        public void setText(String text) {
            
        }
        
    }

    public void openDocumentWindow() {
        window.setText(document.getContent());
        window.openWindow();
    }

    public boolean closeFile() {
        return true;
    }
    
}
