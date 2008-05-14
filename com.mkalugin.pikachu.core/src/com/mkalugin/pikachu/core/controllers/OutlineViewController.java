/**
 * 
 */
package com.mkalugin.pikachu.core.controllers;

import com.mkalugin.pikachu.core.controllers.viewglue.OutlineView;
import com.mkalugin.pikachu.core.controllers.viewglue.OutlineViewCallback;
import com.mkalugin.pikachu.core.controllers.viewglue.OutlineViewFactory;
import com.mkalugin.pikachu.core.model.Document;

public class OutlineViewController implements OutlineViewCallback {
    
    private final OutlineView outlineView;

    public OutlineViewController(Document document, OutlineViewFactory factory) {
        outlineView = factory.bindOutlineView(this);
    }
    
}