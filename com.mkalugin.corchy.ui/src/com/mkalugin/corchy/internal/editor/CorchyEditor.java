package com.mkalugin.corchy.internal.editor;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.widgets.Composite;

public class CorchyEditor {

	private SourceViewer sourceViewer;

	public CorchyEditor(Composite parent) {
		createControls(parent);
	}

	private void createControls(Composite parent) {

		sourceViewer = new CorchyViewer(parent);
		sourceViewer.setDocument(createDocument());

	}

	private Document createDocument() {
		return new Document("Hello\nWorld");
	}

	public void setLayoutData(Object editorData) {
		sourceViewer.getControl().setLayoutData(editorData);
	}

}
