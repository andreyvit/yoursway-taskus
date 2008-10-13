package com.mkalugin.corchy.internal.ui.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationDamager;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.IPresentationRepairer;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.widgets.Display;

import com.mkalugin.corchy.internal.ui.PlatformSpecific;
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
import com.mkalugin.pikachu.core.workspace.DocumentParser;

public class CorchySourceViewerConfiguration extends SourceViewerConfiguration {
    
    private static interface ISemanticHighlighter {
        Collection<StyleRange> highlight(String doc);
    }
    
    private static class SemanticDamagerRepairer implements IPresentationDamager, IPresentationRepairer {
        
        private IDocument document;
        private final ISemanticHighlighter highlighter;
        private Collection<StyleRange> ast;
        
        public SemanticDamagerRepairer(ISemanticHighlighter highlighter) {
            this.highlighter = highlighter;
        }
        
        public IRegion getDamageRegion(ITypedRegion partition, DocumentEvent event,
                boolean documentPartitioningChanged) {
            String oldDoc = document.get();
            ast = highlighter.highlight(oldDoc);
            
            int min = document.getLength();
            int max = -1;
            
            int eventTextLength = event.fText.length();
            for (StyleRange r : ast) {
                if (r.start > event.fOffset + event.fLength + eventTextLength)
                    continue;
                if (r.start + r.length < event.fOffset)
                    continue;
                if (r.start < min)
                    min = r.start;
                if (r.start + r.length > max)
                    max = r.start + r.length;
            }
            
            if (max == -1)
                return new Region(0, 0);
            
            min = Math.max(min - 100, 0);
            max = Math.min(oldDoc.length(), max + 100); // don't kill me! :)
            
            return new Region(min, max - min);
        }
        
        public void setDocument(IDocument document) {
            if (document == null)
                throw new IllegalArgumentException("document is null");
            this.document = document;
        }
        
        public void createPresentation(TextPresentation presentation, ITypedRegion damage) {
            for (StyleRange r : ast) {
                if (r.start > damage.getOffset() + damage.getLength())
                    continue;
                if (r.start + r.length < damage.getOffset())
                    continue;
                presentation.addStyleRange(r);
            }
        }
        
    }
    
    private DocumentStylesheet createStylesheet(Display display) {
        return PlatformSpecific.createStylesheet(display);
    }
    
    @Override
    public IPresentationReconciler getPresentationReconciler(final ISourceViewer sourceViewer) {
        PresentationReconciler presentationReconciler = new PresentationReconciler();
        SemanticDamagerRepairer semanticDamagerRepairer = new SemanticDamagerRepairer(
                new ISemanticHighlighter() {
                    DocumentParser documentParser = new DocumentParser();
                    
                    DocumentStylesheet stylesheet = createStylesheet(sourceViewer.getTextWidget()
                            .getDisplay());
                    
                    public Collection<StyleRange> highlight(String doc) {
                        long tm = System.currentTimeMillis();
                        List<StyleRange> ranges = new ArrayList<StyleRange>();
                        
                        ADocument document = documentParser.parse_old(doc);
                        
                        for (ADocumentLevelNode p : document.getChildren())
                            highlight(ranges, p);
                        System.out.println("hightlight end, time =  " + (System.currentTimeMillis() - tm));
                        return ranges;
                    }
                    
                    private void highlight(final Collection<StyleRange> presentation, ADocumentLevelNode node) {
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
                    
                    protected void highlightProject(Collection<StyleRange> presentation, AProjectLine project) {
                        StyleRange style = new StyleRange();
                        ARange range = project.range();
                        style.start = range.start();
                        style.length = range.length();
                        stylesheet.styleProject(style);
                        presentation.add(style);
                    }
                    
                    protected void highlightText(Collection<StyleRange> presentation, ANode node) {
                        StyleRange style = new StyleRange();
                        ARange range = node.range();
                        style.start = range.start();
                        style.length = range.length();
                        stylesheet.styleText(style);
                        presentation.add(style);
                    }
                    
                    protected void highlightTask(Collection<StyleRange> presentation, ATaskLine task) {
                        boolean isDone = task.isDone();
                        for (ATaskLevelNode node : task.getChildren())
                            highlight(presentation, node, isDone);
                    }
                    
                    private void highlight(final Collection<StyleRange> presentation, ATaskLevelNode node,
                            final boolean isDone) {
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
                    
                    protected void highlightTaskText(Collection<StyleRange> presentation, ATaskLevelNode tag,
                            boolean isDone) {
                        StyleRange style = new StyleRange();
                        ARange range = tag.range();
                        style.start = range.start();
                        style.length = range.length();
                        if (isDone)
                            stylesheet.styleDoneTask(style);
                        else
                            stylesheet.styleTask(style);
                        presentation.add(style);
                    }
                    
                    protected void highlightTaskTag(Collection<StyleRange> presentation, ATag tag) {
                        StyleRange style = new StyleRange();
                        ARange range = tag.range();
                        style.start = range.start();
                        style.length = range.length();
                        stylesheet.styleTag(style);
                        presentation.add(style);
                    }
                    
                });
        presentationReconciler.setDamager(semanticDamagerRepairer, IDocument.DEFAULT_CONTENT_TYPE);
        presentationReconciler.setRepairer(semanticDamagerRepairer, IDocument.DEFAULT_CONTENT_TYPE);
        return presentationReconciler;
    }
    
    @Override
    public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
        return new IAutoEditStrategy[] { new CorchyAutoEditStrategy() };
    }
    
    @Override
    public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
        final ContentAssistant assistant = new ContentAssistant();
        
        assistant.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
        assistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_ABOVE);
        assistant.setInformationControlCreator(getInformationControlCreator(sourceViewer));
        assistant.enableAutoActivation(true);
        assistant.setAutoActivationDelay(0);
        
        assistant.setContentAssistProcessor(new CorchyCompletionProcessor(), IDocument.DEFAULT_CONTENT_TYPE);
        
        return assistant;
    }
    
}
