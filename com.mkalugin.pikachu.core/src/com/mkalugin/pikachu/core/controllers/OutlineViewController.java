/**
 * 
 */
package com.mkalugin.pikachu.core.controllers;

import com.mkalugin.pikachu.core.DocumentListener;
import com.mkalugin.pikachu.core.ast.AProjectName;
import com.mkalugin.pikachu.core.controllers.viewglue.OutlineView;
import com.mkalugin.pikachu.core.controllers.viewglue.OutlineViewCallback;
import com.mkalugin.pikachu.core.controllers.viewglue.OutlineViewFactory;
import com.mkalugin.pikachu.core.model.Document;

public class OutlineViewController implements OutlineViewCallback, DocumentListener {
    
    private final OutlineView outlineView;
	private final Document document;
	private final ProjectSelection projectSelection;

    public OutlineViewController(Document document, ProjectSelection selection, OutlineViewFactory factory) {
        this.document = document;
		this.projectSelection = selection;
		outlineView = factory.bindOutlineView(this);
		pushDocumentToView();
        document.addListener(this);
    }

    private void pushDocumentToView() {    	
    	outlineView.setDocument(document.getDocumentNode());
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

	public void projectSelected(AProjectName name) {
		projectSelection.setToProject(name, this);
	}
    
}