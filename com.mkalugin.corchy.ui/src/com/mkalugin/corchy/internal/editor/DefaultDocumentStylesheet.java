package com.mkalugin.corchy.internal.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;

public class DefaultDocumentStylesheet implements DocumentStylesheet {

	private Font projectFont;
	private Font taskFont;
	private Font tagFont;

	public DefaultDocumentStylesheet() {
		projectFont = new Font(Display.getDefault(), "Helvetica", 16, 0);
		taskFont = new Font(Display.getDefault(), "Helvetica", 13, 0);
		tagFont = new Font(Display.getDefault(), "Helvetica", 13, SWT.ITALIC);
	}

	public void styleProject(TextStyle style) {
		style.underline = true;
		style.font = projectFont;
	}

	public void styleTask(TextStyle style) {
		style.font = taskFont;
	}

	public void styleTag(TextStyle style) {
		style.font = tagFont;
		style.foreground = Display.getDefault().getSystemColor(SWT.COLOR_GRAY);
	}

	public void dispose() {
		projectFont.dispose();
		tagFont.dispose();
		taskFont.dispose();
	}

}
