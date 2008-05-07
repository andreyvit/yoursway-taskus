package com.mkalugin.corchy.internal.editor;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.widgets.Composite;

import com.mkalugin.corchy.ui.core.CorchyApplication;
import com.mkalugin.pikachu.core.model.ModelConsumer;
import com.mkalugin.pikachu.core.storage.StorageException;
import com.mkalugin.pikachu.core.workspace.Workspace;
import com.mkalugin.pikachu.core.workspace.WorkspaceSnapshot;

public class CorchyEditor implements ModelConsumer<WorkspaceSnapshot> {

	private SourceViewer sourceViewer;
	private Document document;
	private long lastUpdateTime;
	private Workspace workspace;

	public CorchyEditor(Composite parent) {
		createControls(parent);
		workspace = CorchyApplication.workspace();
		workspace.registerConsumer(this);
	}

	private void createControls(Composite parent) {
		sourceViewer = new CorchyViewer(parent);
		document = createDocument();
		sourceViewer.setDocument(document);
		document.addDocumentListener(new IDocumentListener() {

			public void documentAboutToBeChanged(DocumentEvent event) {
			}

			public void documentChanged(DocumentEvent event) {
				saveDocument();
			}

		});
	}

	private void saveDocument() {
		String source = document.get();
		lastUpdateTime = System.currentTimeMillis();
		try {
			workspace.pushData(source);
		} catch (StorageException e) {
			// TODO
		}
	}

	private Document createDocument() {
		return new Document("Hello\nWorld");
	}

	public void setLayoutData(Object editorData) {
		sourceViewer.getControl().setLayoutData(editorData);
	}

	public void consume(WorkspaceSnapshot snapshot) {
		if (snapshot.timeStamp() < lastUpdateTime) {
			saveDocument();
			return;
		}
		String currentData = document.get();
		String newContent = snapshot.content();
		if (currentData.equals(newContent))
			return;
		document.set(newContent);
	}

}
