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
		this(parent, true, false);
	}
	
	public OutlineItem(Composite parent, boolean handCursor, boolean initActive) {
		super(parent, SWT.NONE);
		active = initActive;
		font = new Font(Display.getDefault(), "Gill Sans", 14, 0);
		activeFont = new Font(Display.getDefault(), "Gill Sans", 14, SWT.BOLD);
		this.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		this.addPaintListener(new PaintListener() {

			public void paintControl(PaintEvent e) {
				e.gc.setFont(active ? activeFont : font);
				Point textExtent = e.gc.textExtent(text);
				int width = OutlineItem.this.getSize().x;
				if (textExtent.x > width) {
					String newText = text; 
					do {
						newText = text.substring(0, newText.length() - 1);
						textExtent = e.gc.textExtent(newText + "...");
					} while (textExtent.x > width && newText.length() > 0);
//					e.gc.drawText(newText + "...", e.width - textExtent.x, 0);
					e.gc.drawText(newText + "...", 0, 0);
				} else 
					e.gc.drawText(text, 0, 0);
					//e.gc.drawText(text, e.width - textExtent.x, 0);
			}

		});
		if (handCursor)
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
		return new Point(Math.max(computedSize.x, 120), 20);
	}

}
