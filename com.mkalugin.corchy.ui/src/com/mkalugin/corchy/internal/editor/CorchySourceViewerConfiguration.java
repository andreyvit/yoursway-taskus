package com.mkalugin.corchy.internal.editor;

import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

public class CorchySourceViewerConfiguration extends SourceViewerConfiguration {
    
    @Override
    public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
        return null;
    }
    
    @Override
    public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
        return new IAutoEditStrategy[] {new CorchyAutoEditStrategy()};
    }
    
}
