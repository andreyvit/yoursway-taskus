package com.mkalugin.corchy.internal.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class OutlineItem extends Canvas {

	private String text;
	private boolean active;
	private Font font;
	private Font activeFont;
	
	public OutlineItem(Composite parent) {
		super(parent, SWT.NONE);
		font = new Font(Display.getDefault(), "Gill Sans", 16, 0);
		activeFont = new Font(Display.getDefault(), "Gill Sans", 16, SWT.BOLD);
		this.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		this.addPaintListener(new PaintListener() {

			public void paintControl(PaintEvent e) {
				e.gc.setFont(active?activeFont:font);
				Point textExtent = e.gc.textExtent(text);
				e.gc.drawText(text, e.width - textExtent.x, 0);
			}
			
		});
		this.setCursor(Display.getDefault().getSystemCursor(SWT.CURSOR_HAND));
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	@Override
	public Point computeSize(int hint, int hint2, boolean changed) {
		Point computedSize = super.computeSize(hint, hint2, changed);
			return new Point(computedSize.x, 20);
	}
	
}
