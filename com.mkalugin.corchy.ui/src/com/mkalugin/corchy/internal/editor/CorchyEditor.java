package com.mkalugin.corchy.internal.editor;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.mkalugin.corchy.ui.core.CorchyApplication;
import com.mkalugin.pikachu.core.astxxxxx.Project;
import com.mkalugin.pikachu.core.astxxxxx.Tag;
import com.mkalugin.pikachu.core.astxxxxx.TaskHeadline;
import com.mkalugin.pikachu.core.model.ModelConsumer;
import com.mkalugin.pikachu.core.workspace.Workspace;
import com.mkalugin.pikachu.core.workspace.WorkspaceSnapshot;

public class CorchyEditor implements ModelConsumer<WorkspaceSnapshot> {

	private CorchyViewer sourceViewer;
	private Document document;
	private Workspace workspace;
	private boolean consuming;
	private DocumentStylesheet stylesheet;

	public CorchyEditor(Composite parent) {
		createControls(parent);
		workspace = CorchyApplication.workspace();
		workspace.registerConsumer(this);
		consuming = false;
	}

	private void createControls(Composite parent) {
		stylesheet = new DefaultDocumentStylesheet();
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
	
	private boolean isVirgin = true;

	public synchronized void consume(final WorkspaceSnapshot snapshot) {
		consuming = true;
		String current = document.get();
		final String fresh = snapshot.content();
		if (!current.equals(fresh)) {
			Display.getDefault().asyncExec(new Runnable() {

				public void run() {
				    if (isVirgin) {
				        document.set(fresh);
				        isVirgin = false;
				    }
				}

			});

		}
		Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                updateHighlighting(snapshot);
            }
		    
		});
		consuming = false;
	}

	protected void updateHighlighting(WorkspaceSnapshot snapshot) {
		TextPresentation presentation = new TextPresentation();
		for (Project p : snapshot.projects()) {
			highlight(presentation, p);
		}
		sourceViewer.changeTextPresentation(presentation, true);
	}

	protected void highlight(TextPresentation presentation, Project project) {
		StyleRange range = new StyleRange();
		range.start = project.titleStart();
		range.length = project.titleLength();
		range.rise = 5;		
		stylesheet.styleProject(range);
		presentation.addStyleRange(range);
	}

	protected void highlight(TextPresentation presentation, TaskHeadline task) {
		StyleRange range = new StyleRange();
		range.start = task.startOffset();
		range.length = task.length();
		stylesheet.styleTask(range);
		presentation.addStyleRange(range);

	}

	protected void highlight(TextPresentation presentation, Tag tag) {
		StyleRange range = new StyleRange();
		range.start = tag.startOffset();
		range.length = tag.length();
		stylesheet.styleTag(range);
		presentation.addStyleRange(range);
	}

	public void dispose() {
		stylesheet.dispose();
	}
	
}
