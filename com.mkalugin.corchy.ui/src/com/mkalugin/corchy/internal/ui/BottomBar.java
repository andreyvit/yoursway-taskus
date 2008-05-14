package com.mkalugin.corchy.internal.ui;

import static com.mkalugin.corchy.internal.images.CorchyImages.IMG_BOTTOM_BAR;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
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
	
	private void createBottomBar(Composite parent) {
		final Canvas bottomBar = new Canvas(parent, SWT.NONE);
		bottomBar.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL,
				SWT.END).grab(true, false).indent(0, 0)
				.minSize(SWT.DEFAULT, 32).hint(SWT.DEFAULT, 32).create());

		GridLayoutFactory.fillDefaults().numColumns(2).extendedMargins(8, 8, 4, 0).margins(0, 0)
				.spacing(0, 0).generateLayout(bottomBar);
	}
	


	
}
