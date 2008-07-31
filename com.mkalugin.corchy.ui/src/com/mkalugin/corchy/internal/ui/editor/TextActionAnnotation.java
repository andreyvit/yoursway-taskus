package com.mkalugin.corchy.internal.ui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

public abstract class TextActionAnnotation extends ActionAnnotation {

	private boolean inside;
	private final StyledText textWidget;

	public TextActionAnnotation(StyledText styledText) {
		this.textWidget = styledText;
	}
	
	@Override
	public Point computeSize() {
		return new Point(80, 20);
	}

	@Override
	public void render(GC gc, Point offset) {
		gc.setAlpha(50);
		gc.drawRoundRectangle(offset.x, offset.y, computeSize().x, computeSize().y, 10, 10);
		if (inside) {
			gc.setBackground(Display.getDefault()
					.getSystemColor(SWT.COLOR_BLUE));
			gc.fillRoundRectangle(offset.x, offset.y, computeSize().x, computeSize().y, 10, 10);
		}
		gc.setAlpha(255);
		gc.setFont(new Font(Display.getDefault(), "Gill Sans", 14, 0));
		Point textExtent = gc.textExtent(getText());
		gc
				.drawText(
						getText(),
						(int) (offset.x + computeSize().x / 2.0 - textExtent.x / 2.0),
						offset.y + 3, true);
	}

	@Override
	public void mouseEnter(MouseEvent e) {
		inside = true;
		textWidget.setCursor(Display.getDefault().getSystemCursor(
				SWT.CURSOR_ARROW));
		textWidget.redraw();
	}

	@Override
	public void mouseExit(MouseEvent e) {
		inside = false;
		textWidget.setCursor(Display.getDefault().getSystemCursor(
				SWT.CURSOR_IBEAM));
		textWidget.redraw();
	}
	
	protected abstract String getText();
	
}
