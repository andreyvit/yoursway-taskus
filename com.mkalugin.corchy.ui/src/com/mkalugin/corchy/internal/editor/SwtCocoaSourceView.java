package com.mkalugin.corchy.internal.editor;

import static com.google.common.collect.Lists.newArrayList;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.widgets.Composite;

import com.mkalugin.pikachu.core.ast.ADocument;
import com.mkalugin.pikachu.core.ast.ADocumentLevelNode;
import com.mkalugin.pikachu.core.ast.ADocumentLevelVisitor;
import com.mkalugin.pikachu.core.ast.AEmptyLine;
import com.mkalugin.pikachu.core.ast.ANode;
import com.mkalugin.pikachu.core.ast.AProjectLine;
import com.mkalugin.pikachu.core.ast.ARange;
import com.mkalugin.pikachu.core.ast.ATag;
import com.mkalugin.pikachu.core.ast.ATaskDescriptionFragment;
import com.mkalugin.pikachu.core.ast.ATaskLeader;
import com.mkalugin.pikachu.core.ast.ATaskLevelNode;
import com.mkalugin.pikachu.core.ast.ATaskLevelVisitor;
import com.mkalugin.pikachu.core.ast.ATaskLine;
import com.mkalugin.pikachu.core.ast.ATaskName;
import com.mkalugin.pikachu.core.ast.ATextLine;
import com.mkalugin.pikachu.core.controllers.search.SearchResult;
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
        stylesheet = new DefaultDocumentStylesheet(parent.getDisplay());
        sourceViewer = new CorchyViewer(parent);
        sourceViewer.getControl().setFocus(); 
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
    
    private boolean settingPresentationFirstTime = true;
    
    protected void updateHighlighting(ADocument document) {
        TextPresentation presentation = new TextPresentation();
        for (ADocumentLevelNode p : document.getChildren())
            highlight(presentation, p);
        final ArrayList<StyleRange> ranges = newArrayList((Iterator<StyleRange>) presentation
                .getAllStyleRangeIterator());
        Runnable runnable = new Runnable() {
            
            public void run() {
                sourceViewer.getTextWidget().setStyleRanges(
                        (StyleRange[]) ranges.toArray(new StyleRange[ranges.size()]));
            }
            
        };
        if (settingPresentationFirstTime) {
            sourceViewer.getTextWidget().getDisplay().asyncExec(runnable);
            settingPresentationFirstTime = false;
        } else
            runnable.run();
        //        sourceViewer.changeTextPresentation(presentation, true);
    }
    
    private void highlight(final TextPresentation presentation, ADocumentLevelNode node) {
        node.accept(new ADocumentLevelVisitor() {
            
            public void visitEmptyLine(AEmptyLine line) {
                highlightText(presentation, line);
            }
            
            public void visitProjectLine(AProjectLine line) {
                highlightProject(presentation, line);
            }
            
            public void visitTaskLine(ATaskLine line) {
                highlightTask(presentation, line);
            }
            
            public void visitTextLine(ATextLine line) {
                highlightText(presentation, line);
            }
            
        });
    }
    
    protected void highlightProject(TextPresentation presentation, AProjectLine project) {
        StyleRange style = new StyleRange();
        ARange range = project.range();
        style.start = range.start();
        style.length = range.length();
        style.rise = 5;
        stylesheet.styleProject(style);
        presentation.addStyleRange(style);
        
        //        try {
        //            int line = document.getLineOfOffset(range.start());
        //            sourceViewer.getTextWidget().setLineBackground(line, 1, 
        //                    sourceViewer.getTextWidget().getDisplay().getSystemColor(SWT.COLOR_BLUE));
        //        } catch (BadLocationException e) {
        //            e.printStackTrace();
        //        }
    }
    
    protected void highlightText(TextPresentation presentation, ANode node) {
        StyleRange style = new StyleRange();
        ARange range = node.range();
        style.start = range.start();
        style.length = range.length();
        stylesheet.styleText(style);
        presentation.addStyleRange(style);
    }
    
    protected void highlightTask(TextPresentation presentation, ATaskLine task) {
        boolean isDone = task.isDone();
        for (ATaskLevelNode node : task.getChildren())
            highlight(presentation, node, isDone);
    }
    
    private void highlight(final TextPresentation presentation, ATaskLevelNode node, final boolean isDone) {
        node.accept(new ATaskLevelVisitor() {
            
            public void visitDescriptionFragment(ATaskDescriptionFragment fragment) {
                highlightTaskText(presentation, fragment, false);
            }
            
            public void visitLeader(ATaskLeader leader) {
                highlightTaskText(presentation, leader, false);
            }
            
            public void visitName(ATaskName name) {
                highlightTaskText(presentation, name, isDone);
            }
            
            public void visitTag(ATag tag) {
                highlightTaskTag(presentation, tag);
            }
            
        });
    }
    
    protected void highlightTaskText(TextPresentation presentation, ATaskLevelNode tag, boolean isDone) {
        StyleRange style = new StyleRange();
        ARange range = tag.range();
        style.start = range.start();
        style.length = range.length();
        if (isDone)
            stylesheet.styleDoneTask(style);
        else
            stylesheet.styleTask(style);
        presentation.addStyleRange(style);
    }
    
    protected void highlightTaskTag(TextPresentation presentation, ATag tag) {
        StyleRange style = new StyleRange();
        ARange range = tag.range();
        style.start = range.start();
        style.length = range.length();
        stylesheet.styleTag(style);
        presentation.addStyleRange(style);
    }
    
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
    
    public void highlightAccordingTo(ADocument documentNode) {
        updateHighlighting(documentNode);
    }
 
    public void highlightSearchResults(SearchResult result) {
		sourceViewer.highlightSearchResults(result);
	}
	
	public void highlightMatch(int number) {
		sourceViewer.highlightMatch(number);
	}
    
}
