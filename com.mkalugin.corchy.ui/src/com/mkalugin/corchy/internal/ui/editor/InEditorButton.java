package com.mkalugin.corchy.internal.ui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class InEditorButton extends Canvas {

	private String text;
	private Font font;
	
	public InEditorButton(Composite parent) {
		super(parent, SWT.NONE);
		font = new Font(Display.getDefault(), "Gill Sans", 13, SWT.BOLD);
		this.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		this.addPaintListener(new PaintListener() {

			public void paintControl(PaintEvent e) {
				e.gc.setFont(font);
				e.gc.setAlpha(50);
				e.gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_BLUE));
				e.gc.fillRoundRectangle(e.x, e.y, e.width, e.height, 10, 10);
				Point textExtent = e.gc.textExtent(text);
				e.gc.setAlpha(100);
				e.gc.drawText(text, (e.width - textExtent.x)/2, (e.height - textExtent.y)/2, true);
				e.gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
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
//	
//	@Override
//	public Point computeSize(int hint, int hint2, boolean changed) {
//		Point computedSize = super.computeSize(hint, hint2, changed);
//			return new Point(computedSize.x, 15);
//	}
}
