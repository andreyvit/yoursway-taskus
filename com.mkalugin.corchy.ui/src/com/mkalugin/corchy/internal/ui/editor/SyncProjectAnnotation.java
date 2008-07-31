package com.mkalugin.corchy.internal.ui.editor;

import org.eclipse.swt.custom.StyledText;

import com.mkalugin.pikachu.core.controllers.viewglue.SourceViewCallback;
import com.mkalugin.pikachu.core.model.document.structure.MProject;

public class SyncProjectAnnotation extends TextActionAnnotation {

	private final MProject project;
	private final SourceViewCallback callback;

	public SyncProjectAnnotation(StyledText styledText, SourceViewCallback callback, MProject project) {
		super(styledText);
		this.callback = callback;
		this.project = project;
	}

	@Override
	protected String getText() {
		return "Sync Now";
	}
	
	@Override
	public void doAction() {
		callback.syncProject(project.getLine().name());
	}

}
