package com.mkalugin.corchy.internal.ui;

import static com.mkalugin.corchy.internal.images.CorchyImages.IMG_BOTTOM_BAR;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class BottomBar extends Canvas {
	
	public BottomBar(Composite parent) {
        super(parent, SWT.NONE);
        addPaintListener(new PaintListener() {
            
            public void paintControl(PaintEvent e) {
                GC gc = e.gc;
                gc.setClipping((Rectangle)null);
                Pattern bottomBarPattern = new Pattern(Display.getCurrent(), IMG_BOTTOM_BAR.get());
                gc.setBackgroundPattern(bottomBarPattern);
                gc.fillRectangle(getClientArea());
            }
            
        });
    }
	
	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
	    Point size = super.computeSize(wHint, hHint, changed);
	    if (size.y < 32)
	        size.y = 32;
        return size;
	}
	
}
