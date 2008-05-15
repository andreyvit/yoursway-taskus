package com.mkalugin.corchy.internal.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;

public class DefaultDocumentStylesheet implements DocumentStylesheet {

	private Font projectFont;
	private Font taskFont;
	private Font tagFont;
    private Font textFont;
    private Color tagColor;
    private Color textColor;
    private final Display display;

	public DefaultDocumentStylesheet(Display display) {
        this.display = display;
        projectFont = new Font(display, "Helvetica", 16, SWT.BOLD);
		textFont = new Font(display, "Helvetica", 13, 0);
		taskFont = new Font(display, "Helvetica", 13, 0);
		tagFont = new Font(display, "Helvetica", 13, SWT.ITALIC);
		tagColor = display.getSystemColor(SWT.COLOR_GRAY);
		textColor = display.getSystemColor(SWT.COLOR_DARK_GRAY);
	}

	public void styleProject(TextStyle style) {
		style.underline = true;
		style.font = projectFont;
//		Color projectForegroundColor = display.getSystemColor(SWT.COLOR_WHITE);
//		Color projectBackgroundColor = display.getSystemColor(SWT.COLOR_BLACK);
//		style.background = projectBackgroundColor;
//		style.foreground = projectForegroundColor;
	}

	public void styleTask(TextStyle style) {
		style.font = taskFont;
	}

	public void styleTag(TextStyle style) {
		style.font = tagFont;
        style.foreground = tagColor;
	}
	
	public void styleText(TextStyle style) {
        style.font = tagFont;
        style.foreground = textColor;
	}

	public void dispose() {
		projectFont.dispose();
		tagFont.dispose();
		taskFont.dispose();
		textFont.dispose();
	}

}
