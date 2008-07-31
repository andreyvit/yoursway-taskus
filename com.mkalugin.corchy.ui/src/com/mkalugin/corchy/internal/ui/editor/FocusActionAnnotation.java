package com.mkalugin.corchy.internal.ui.editor;

import org.eclipse.swt.custom.StyledText;

import com.mkalugin.pikachu.core.model.document.structure.MProject;

public class FocusActionAnnotation extends TextActionAnnotation {

	private final MProject project;

	public FocusActionAnnotation(StyledText styledText, MProject project) {
		super(styledText);
		this.project = project;
	}

	@Override
	protected String getText() {
		return "Focus";
	}

}
