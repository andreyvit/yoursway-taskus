/**
 * 
 */
package com.mkalugin.pikachu.core.controllers;

import com.mkalugin.pikachu.core.DocumentListener;
import com.mkalugin.pikachu.core.ast.AProjectName;
import com.mkalugin.pikachu.core.ast.ARange;
import com.mkalugin.pikachu.core.controllers.viewglue.SourceView;
import com.mkalugin.pikachu.core.controllers.viewglue.SourceViewCallback;
import com.mkalugin.pikachu.core.controllers.viewglue.SourceViewFactory;
import com.mkalugin.pikachu.core.model.Document;

public class SourceViewController implements SourceViewCallback, DocumentListener,
		ProjectSelectionListener {

	private SourceView sourceView;
	private final Document document;
	private final ProjectSelection projectSelection;

	public SourceViewController(Document document, ProjectSelection projectSelection,
			SourceViewFactory factory) {
		this.document = document;
		this.projectSelection = projectSelection;
		projectSelection.addListener(this);
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
			updateHighlighting();
		else
			setTextToControl();
	}

	private void setTextToControl() {
		sourceView.setText(document.getContent());
		updateHighlighting();
	}

	private void updateHighlighting() {
		sourceView.highlightAccordingTo(document.getDocumentNode());
	}

	public void closed(boolean discarded) {
	}

	public void emptinessChanged() {
	}

	public void selectionChanged(int start, int end) {
		// TODO
	}

	public void projectSelectionChanged(Object sender) {
		if (sender != this) {
			AProjectName selectedProject = projectSelection.selectedProject();
			ARange range = selectedProject.range();
			sourceView.setSelection(range.start(), range.end());
		}
	}

}