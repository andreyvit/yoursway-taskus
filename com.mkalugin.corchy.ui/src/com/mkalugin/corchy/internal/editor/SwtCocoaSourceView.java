package com.mkalugin.corchy.internal.editor;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.swt.widgets.Composite;

import com.mkalugin.pikachu.core.controllers.viewglue.DocumentWindowCallback;
import com.mkalugin.pikachu.core.controllers.viewglue.SourceView;
import com.mkalugin.pikachu.core.controllers.viewglue.SourceViewCallback;

public class SwtCocoaSourceView implements SourceView {

	private CorchyViewer sourceViewer;
	private Document document;
//	private Workspace workspace;
	private boolean consuming;
	private DocumentStylesheet stylesheet;
    private SourceViewCallback callback;

	public SwtCocoaSourceView(Composite parent) {
        createControls(parent);
//		workspace = CorchyApplication.workspace();
//		workspace.registerConsumer(this);
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

	protected void saveDocument() {
	    callback.setText(document.get());
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
	
//	public synchronized void consume(final WorkspaceSnapshot snapshot) {
//		consuming = true;
//		String current = document.get();
//		final String fresh = snapshot.content();
//		if (!current.equals(fresh)) {
//			Display.getDefault().asyncExec(new Runnable() {
//
//				public void run() {
//				    if (isVirgin) {
//				        document.set(fresh);
//				        isVirgin = false;
//				    }
//				}
//
//			});
//
//		}
//		Display.getDefault().asyncExec(new Runnable() {
//
//            public void run() {
//                updateHighlighting(snapshot);
//            }
//		    
//		});
//		consuming = false;
//	}
//
//	protected void updateHighlighting(WorkspaceSnapshot snapshot) {
//		TextPresentation presentation = new TextPresentation();
//		for (Project p : snapshot.projects()) {
//			highlight(presentation, p);
//		}
//		sourceViewer.changeTextPresentation(presentation, true);
//	}
//
//	protected void highlight(TextPresentation presentation, Project project) {
//		StyleRange range = new StyleRange();
//		range.start = project.titleStart();
//		range.length = project.titleLength();
//		range.rise = 5;		
//		stylesheet.styleProject(range);
//		presentation.addStyleRange(range);
//	}
//
//	protected void highlight(TextPresentation presentation, TaskHeadline task) {
//		StyleRange range = new StyleRange();
//		range.start = task.startOffset();
//		range.length = task.length();
//		stylesheet.styleTask(range);
//		presentation.addStyleRange(range);
//
//	}
//
//	protected void highlight(TextPresentation presentation, Tag tag) {
//		StyleRange range = new StyleRange();
//		range.start = tag.startOffset();
//		range.length = tag.length();
//		stylesheet.styleTag(range);
//		presentation.addStyleRange(range);
//	}

	public void dispose() {
		stylesheet.dispose();
	}

    public void setText(String text) {
        document.set(text);
    }

    public SourceView bind(SourceViewCallback callback) {
        if (callback == null)
            throw new NullPointerException("callback is null");
        if (this.callback != null)
            throw new IllegalStateException("callback is already set");
        this.callback = callback;
        return this;
    }
	
}
