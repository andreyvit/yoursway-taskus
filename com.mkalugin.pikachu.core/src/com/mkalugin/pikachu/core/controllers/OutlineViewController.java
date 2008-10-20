/**
 * 
 */
package com.mkalugin.pikachu.core.controllers;

import com.mkalugin.pikachu.core.DocumentListener;
import com.mkalugin.pikachu.core.controllers.viewglue.OutlineView;
import com.mkalugin.pikachu.core.controllers.viewglue.OutlineViewCallback;
import com.mkalugin.pikachu.core.controllers.viewglue.OutlineViewFactory;
import com.mkalugin.pikachu.core.model.Document;
import com.mkalugin.pikachu.core.model.document.Element;

public class OutlineViewController implements OutlineViewCallback, DocumentListener, SelectionListener {
    
    private final OutlineView outlineView;
    private final Document document;
    private final Selection selection;
    
    public OutlineViewController(Document document, Selection selection, OutlineViewFactory factory) {
        this.document = document;
        this.selection = selection;
        selection.addListener(this);
        outlineView = factory.bindOutlineView(this);
        pushDocumentToView();
        document.addListener(this);
    }
    
    private void pushDocumentToView() {
        outlineView.setDocument(document.getContentModel());
    }
    
    public void bindingChanged() {
    }
    
    public void closed(boolean discarded) {
    }
    
    public void contentChanged(Object sender) {
        pushDocumentToView();
    }
    
    public void emptinessChanged() {
    }
    
    public void elementSelected(Element element) {
        selection.setTo(element, this);
    }
    
    public void selectionChanged(Object sender) {
        if (sender != this)
            outlineView.setActiveItem(selection.selectedElement());
    }
    
}