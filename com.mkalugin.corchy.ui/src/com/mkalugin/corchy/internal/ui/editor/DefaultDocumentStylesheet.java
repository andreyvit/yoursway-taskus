package com.mkalugin.corchy.internal.ui.editor;

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

	public DefaultDocumentStylesheet(Display display) {
        projectFont = new Font(display, "Georgia", 24, SWT.BOLD);
		textFont = new Font(display, "Gill Sans", 14, 0);
		taskFont = new Font(display, "Gill Sans", 14, 0);
		tagFont = new Font(display, "Gill Sans", 14, 0);
		tagColor = display.getSystemColor(SWT.COLOR_GRAY);
		textColor = display.getSystemColor(SWT.COLOR_DARK_GRAY);
	}

	public void styleProject(TextStyle style) {
//		style.underline = true;
		style.font = projectFont;
	}

	public void styleTaskLeader(TextStyle style) {
		style.font = taskFont;
	}
	
	public void styleTask(TextStyle style) {
	    style.font = taskFont;
	}
	
	public void styleDoneTask(TextStyle style) {
	    style.font = taskFont;
	    style.strikeout = true;
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
