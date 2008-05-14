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
    private final Document model;
    private String key;

    public DocumentWindowController(Document model, DocumentWindowFactory factory) {
        if (model == null)
            throw new NullPointerException("model is null");
        this.model = model;
        this.window = factory.createDocumentWindow(this);
        new OutlineViewController(window);
        new SourceViewController(window);
        key = "untitled" + System.currentTimeMillis();
    }

    public void startSynchronization() {
    }

    class OutlineViewController implements OutlineViewCallback {
        
        private final OutlineView outlineView;

        public OutlineViewController(OutlineViewFactory factory) {
            outlineView = factory.bindOutlineView(this);
        }
        
    }
    
    class SourceViewController implements SourceViewCallback {
        
        private SourceView sourceView;

        public SourceViewController(SourceViewFactory factory) {
            sourceView = factory.bindSourceView(this);
        }

        public void setText(String text) {
            // TODO Auto-generated method stub
            
        }
        
    }

    public void openDocumentWindow() {
        window.setText(model.getContent());
        window.openWindow();
    }

    public String uniqueDocumentKeyForPreferencePersistance() {
        return key;
    }
    
}
