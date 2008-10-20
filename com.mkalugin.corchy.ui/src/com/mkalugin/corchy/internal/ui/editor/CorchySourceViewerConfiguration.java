package com.mkalugin.corchy.internal.ui.editor;

import java.util.ArrayList;
import java.util.Collection;

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
import com.mkalugin.pikachu.core.model.document.Chapter;
import com.mkalugin.pikachu.core.model.document.Directive;
import com.mkalugin.pikachu.core.model.document.DocumentModelVisitor;
import com.mkalugin.pikachu.core.model.document.Group;
import com.mkalugin.pikachu.core.model.document.Range;
import com.mkalugin.pikachu.core.model.document.Section;
import com.mkalugin.pikachu.core.model.document.Tag;
import com.mkalugin.pikachu.core.model.document.TaggedContainer;
import com.mkalugin.pikachu.core.model.document.Task;
import com.mkalugin.pikachu.core.model.document.TextLine;
import com.mkalugin.pikachu.core.workspace.DocumentParser;

public class CorchySourceViewerConfiguration extends SourceViewerConfiguration {
    
    private static interface ISemanticHighlighter {
        Collection<StyleRange> highlight(String doc);
    }

    private final class SemanticHighlighter implements ISemanticHighlighter {
        DocumentStylesheet stylesheet;
        
        private SemanticHighlighter(ISourceViewer sourceViewer) {
            stylesheet = createStylesheet(sourceViewer.getTextWidget().getDisplay());
        }
        
        public Collection<StyleRange> highlight(String doc) {
            long tm = System.currentTimeMillis();
            
            final Collection<StyleRange> ranges = new ArrayList<StyleRange>();
            
            TaggedContainer contentModel = DocumentParser.parse(doc);
            
            contentModel.accept(new DocumentModelVisitor() {
                
                public void visit(Chapter chapter) {
                    highlightChapter(ranges, chapter);
                }
                
                public void visit(Section section) {
                    highlightSection(ranges, section);
                }
                
                public void visit(Group group) {
                    highlightGroup(ranges, group);
                }
                
                public void visit(Task task) {
                    highlightTask(ranges, task);
                }
                
                public void visit(TextLine line) {
                    highlightTextLine(ranges, line);
                }
                
                public void visit(Tag tag) {
                    highlightTag(ranges, tag);
                }
                
                public void visit(Directive directive) {
                    highlightDirective(ranges, directive);
                }
                
            });
            
            System.out.println("hightlight end, time =  " + (System.currentTimeMillis() - tm));
            
            return ranges;
        }
        
        private StyleRange createStyle(Range range) {
            StyleRange style = new StyleRange();
            style.start = range.start();
            style.length = range.length();
            return style;
        }
        
        private void highlightChapter(Collection<StyleRange> presentation, Chapter chapter) {
            // TODO Auto-generated method stub
            
        }
        
        private void highlightSection(Collection<StyleRange> presentation, Section section) {
            // TODO Auto-generated method stub
            
        }
        
        private void highlightGroup(Collection<StyleRange> presentation, Group group) {
            StyleRange style = createStyle(group.getNameToken().range());
            stylesheet.styleGroup(style);
            presentation.add(style);
        }
        
        private void highlightTask(Collection<StyleRange> presentation, Task task) {
            StyleRange style = createStyle(task.getNameToken().range());
            
            if (task.isDone())
                stylesheet.styleDoneTask(style);
            else
                stylesheet.styleTask(style);
            
            presentation.add(style);
        }
        
        private void highlightTag(Collection<StyleRange> presentation, Tag tag) {
            StyleRange style = createStyle(tag.range());
            stylesheet.styleTag(style);
            presentation.add(style);
        }
        
        private void highlightTextLine(Collection<StyleRange> presentation, TextLine line) {
            StyleRange style = createStyle(line.range());
            stylesheet.styleText(style);
            presentation.add(style);
        }
        
        private void highlightDirective(Collection<StyleRange> presentation, Directive directive) {
            // TODO Auto-generated method stub
            
        }
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
                new SemanticHighlighter(sourceViewer));
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
