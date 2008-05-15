/**
 * 
 */
package com.mkalugin.pikachu.core.controllers;

import java.io.IOException;

import com.mkalugin.pikachu.core.DocumentListener;
import com.mkalugin.pikachu.core.model.Document;

public class DocumentSavingAgent implements DocumentListener {
    
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

    public void dispose() {
        document.removeListener(this);
    }

    public void closed(boolean discarded) {
    }

    public void emptinessChanged() {
    }
    
}