package com.mkalugin.corchy.internal.ui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;

public class MacDocumentStylesheet implements DocumentStylesheet {

	private Font projectFont;
	private Font taskFont;
	private Font tagFont;
    private Font textFont;
    private Color tagColor;
    private Color textColor;

	public MacDocumentStylesheet(Display display) {
        projectFont = new Font(display, "Georgia", 19, SWT.NONE);
		textFont = new Font(display, "Gill Sans", 14, 0);
		taskFont = new Font(display, "Gill Sans", 14, 0);
		tagFont = new Font(display, "Gill Sans", 14, 0);
		tagColor = display.getSystemColor(SWT.COLOR_DARK_GRAY);
		textColor = new Color(display, 70, 70, 70);
	}

	public void styleGroup(TextStyle style) {
//		style.underline = true;s
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
		textColor.dispose();
	}

}
