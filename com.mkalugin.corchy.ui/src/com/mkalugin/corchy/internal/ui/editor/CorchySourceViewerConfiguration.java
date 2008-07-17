package com.mkalugin.corchy.internal.ui.editor;

import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
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
		return new IAutoEditStrategy[] { new CorchyAutoEditStrategy() };
	}

	@Override
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {

		final ContentAssistant assistant = new ContentAssistant();
		
		assistant.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
//			assistant.setRestoreCompletionProposalSize(getSettings("completion_proposal_size")); //$NON-NLS-1$
		assistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_ABOVE);
		assistant.setInformationControlCreator(getInformationControlCreator(sourceViewer));
		assistant.enableAutoActivation(true);

		
		alterContentAssistant(assistant);

//			getContentAssistPreference().configure(assistant, fPreferenceStore);

		return assistant;
	}

	protected void alterContentAssistant(ContentAssistant assistant) {
		assistant.setContentAssistProcessor(new CorchyCompletionProcessor(),
				IDocument.DEFAULT_CONTENT_TYPE);
	}
	
}
