/**
 * 
 */
package com.mkalugin.pikachu.core.controllers;

import java.util.List;

import com.google.common.collect.Lists;
import com.mkalugin.pikachu.core.DocumentListener;
import com.mkalugin.pikachu.core.ast.ADocument;
import com.mkalugin.pikachu.core.ast.AProjectName;
import com.mkalugin.pikachu.core.controllers.SynchronizationController.SynchronizationDefinition;
import com.mkalugin.pikachu.core.controllers.viewglue.SourceView;
import com.mkalugin.pikachu.core.controllers.viewglue.SourceViewCallback;
import com.mkalugin.pikachu.core.controllers.viewglue.SourceViewFactory;
import com.mkalugin.pikachu.core.model.Document;
import com.mkalugin.pikachu.core.model.document.DocumentContent;
import com.mkalugin.pikachu.core.model.document.Element;
import com.mkalugin.pikachu.core.model.document.Range;
import com.mkalugin.pikachu.core.model.document.structure.MDocument;
import com.mkalugin.pikachu.core.model.document.structure.MElement;
import com.mkalugin.pikachu.core.model.document.structure.MProject;
import com.mkalugin.pikachu.core.model.document.structure.builder.StructuredModelBuilder;

public class SourceViewController implements SourceViewCallback, DocumentListener, SelectionListener {
    
    private final class SelectedElementDocumentModelVisitor extends AbstractDocumentModelVisitor {
        
        private final int start;
        private final int end;
        
        private Element el = null;
        
        private SelectedElementDocumentModelVisitor(int start, int end) {
            this.start = start;
            this.end = end;
        }
        
        @Override
        protected void visitElement(Element element) {
            if (element.range().contains(start, end))
                if (el == null || el.range().contains(element.range()))
                    el = element;
        }
        
        public Element selectedElement() {
            return el;
        }
        
    }
    
    private final SourceView sourceView;
    private final Document document;
    private final Selection selection;
    private final DocumentWindowController documentWindowController;
    
    public SourceViewController(Document document, Selection selection, SourceViewFactory factory,
            DocumentWindowController documentWindowController) {
        this.document = document;
        this.selection = selection;
        this.documentWindowController = documentWindowController;
        selection.addListener(this);
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
    
    public void selectionChanged(final int start, final int end) {
        DocumentContent contentModel = document.getContentModel();
        
        SelectedElementDocumentModelVisitor visitor = new SelectedElementDocumentModelVisitor(start, end);
        contentModel.accept(visitor);
        
        selection.setTo(visitor.selectedElement(), this);
    }
    
    public void selectionChanged(Object sender) {
        if (sender != this) {
            Range range = selection.selectionRange();
            sourceView.setSelection(range.start(), range.end());
        }
    }
    
    public void syncProject(AProjectName projectName) {
        documentWindowController.startSynchronization(projectName);
    }
    
    public boolean projectSyncable(MProject project) {
        ADocument ast = document.getDocumentNode();
        MDocument structure = new StructuredModelBuilder().buildStructure(ast);
        for (MElement element : structure.getChildren())
            if (element instanceof MProject) {
                if (((MProject) element).getLine().name().equals(project.getLine().name())) {
                    List<SynchronizationDefinition> synchronizationDefinitions = Lists.newArrayList();
                    SynchronizationController.collectSynchronizationDefinitions(project,
                            synchronizationDefinitions);
                    return !synchronizationDefinitions.isEmpty();
                }
            }
        return false;
    }
    
}