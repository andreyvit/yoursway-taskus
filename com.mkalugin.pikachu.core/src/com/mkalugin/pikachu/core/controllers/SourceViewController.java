/**
 * 
 */
package com.mkalugin.pikachu.core.controllers;

import com.mkalugin.pikachu.core.DocumentListener;
import com.mkalugin.pikachu.core.controllers.viewglue.SourceView;
import com.mkalugin.pikachu.core.controllers.viewglue.SourceViewCallback;
import com.mkalugin.pikachu.core.controllers.viewglue.SourceViewFactory;
import com.mkalugin.pikachu.core.model.Document;

public class SourceViewController implements SourceViewCallback, DocumentListener {
    
    private SourceView sourceView;
    private final Document document;

    public SourceViewController(Document document, SourceViewFactory factory) {
        this.document = document;
        sourceView = factory.bindSourceView(this);
        setTextToControl();
        document.addListener(this);
    }

    public void setText(String text) {
        document.setContent(text, this);
    }

    public void bindingChanged() {
    }

    public void contentChanged(Object sender) {
        if (sender == this)
            return;
        setTextToControl();
    }
    
    private void setTextToControl() {
        sourceView.setText(this.document.getContent());
    }

    public void closed(boolean discarded) {
    }

}