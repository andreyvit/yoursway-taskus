package com.mkalugin.corchy.internal.ui.editor;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

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
		final ContentAssistant assistant = new ContentAssistant() {
			public String showPossibleCompletions() {
				Shell shell = new Shell(SWT.ON_TOP);
				shell.setSize(300, 200);
				
				List list = new List(shell, SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
				list.add("a");
				list.add("bar");
				list.add("bar");
				list.add("bar");
				list.add("bar");
				list.add("bar");
				list.add("bar");
				list.add("bar");
				list.add("bar");
				list.add("bar");
				list.add("bar");
				list.add("bar");
				list.add("bar");
				list.add("bar");
				list.add("bar");
				list.add("bar");
				list.add("bar");
				list.select(0);
				
				list.setLayoutData(GridDataFactory.fillDefaults().hint(250, 200).minSize(250, 200).align(SWT.FILL, SWT.FILL).grab(true, true).create());
				
				GridLayoutFactory.fillDefaults().margins(0, 0).spacing(0, 0).generateLayout(shell);
				
				shell.open();
				shell.setActive();
				
				list.setFocus();
				
				
				return null;
			}
		};

		assistant.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
//			assistant.setRestoreCompletionProposalSize(getSettings("completion_proposal_size")); //$NON-NLS-1$
		assistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_ABOVE);
		assistant.setInformationControlCreator(getInformationControlCreator(sourceViewer));
		assistant.enableAutoActivation(true);
//		
//		sourceViewer.getTextWidget().addKeyListener(new KeyListener() {
//
//			public void keyPressed(KeyEvent e) {
//				if (e.character == '@') {
//					assistant.showPossibleCompletions();
//				}
//			}
//
//			public void keyReleased(KeyEvent e) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//		})	;	
		
		alterContentAssistant(assistant);

//			getContentAssistPreference().configure(assistant, fPreferenceStore);

		return assistant;
	}

	protected void alterContentAssistant(ContentAssistant assistant) {
		assistant.setContentAssistProcessor(new CorchyCompletionProcessor(),
				IDocument.DEFAULT_CONTENT_TYPE);
	}
	
}
