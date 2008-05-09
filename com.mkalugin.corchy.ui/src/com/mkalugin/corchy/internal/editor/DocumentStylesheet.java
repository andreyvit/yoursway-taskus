package com.mkalugin.corchy.internal.editor;

import org.eclipse.swt.graphics.TextStyle;

public interface DocumentStylesheet {

	void styleProject(TextStyle style);

	void styleTask(TextStyle style);

	void styleTag(TextStyle style);
	
	void dispose();

}