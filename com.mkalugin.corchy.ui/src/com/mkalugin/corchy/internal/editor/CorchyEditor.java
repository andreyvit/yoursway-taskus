package com.mkalugin.corchy.internal.editor;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.mkalugin.corchy.ui.core.CorchyApplication;
import com.mkalugin.pikachu.core.model.ModelConsumer;
import com.mkalugin.pikachu.core.workspace.Workspace;
import com.mkalugin.pikachu.core.workspace.WorkspaceSnapshot;

public class CorchyEditor implements ModelConsumer<WorkspaceSnapshot> {

	private CorchyViewer sourceViewer;
	private Document document;
	private Workspace workspace;
	private boolean consuming;

	public CorchyEditor(Composite parent) {
		createControls(parent);
		workspace = CorchyApplication.workspace();
		workspace.registerConsumer(this);
		consuming = false;
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
		// do not fall into infinite recursion due to ws changes
		if (consuming)
			return;
		workspace.pushData(document.get());
	}

	private Document createDocument() {
		return new Document("");
	}

	public void setLayoutData(Object editorData) {
		sourceViewer.getControl().setLayoutData(editorData);
	}

	public boolean isActive() {
		return sourceViewer.getControl().isFocusControl();
	}

	public void undo() {
		if (sourceViewer.getUndoManager().undoable())
			sourceViewer.getUndoManager().undo();
	}

	public void redo() {
		if (sourceViewer.getUndoManager().redoable())
			sourceViewer.getUndoManager().redo();
	}

	public synchronized void consume(final WorkspaceSnapshot snapshot) {
		consuming = true;
		String current = document.get();
		final String fresh = snapshot.content();
		if (!current.equals(fresh)){
			Display.getDefault().syncExec(new Runnable() {

				public void run() {
					document.set(fresh);					
					updateHighlighting(snapshot.ast());
				}
				
			});
			
		}
		consuming = false;
	}

	protected void updateHighlighting(String[] ast) {
		TextPresentation presentation = new TextPresentation();
		TextStyle style = new TextStyle();
		style.font = new Font(Display.getCurrent(), "Helvetica", 18, 0);
		StyleRange range = new StyleRange(style);
		range.start = 2;
		range.length = 4;
		presentation.addStyleRange(range);
		sourceViewer.changeTextPresentation(presentation, true);
	}

}
