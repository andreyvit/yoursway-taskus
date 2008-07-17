package com.mkalugin.corchy.ui.controls;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class BottomBarComposition {
    
	private static int BOTTOM_BAR_SIZE = 32;
	
    private Composite body;
    private Composite bottomBar;
    
    public BottomBarComposition(Shell parent) {
//    	NSWindow window = parent.view.window();
//    	window.setContentBorderThickness(BOTTOM_BAR_SIZE, OS.NSMinYEdge);
    	
    	// TODO: add bg image
    	
        body = new Composite(parent, SWT.NONE);
        body.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        
        bottomBar = new Composite(parent, SWT.NONE);
        bottomBar.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).minSize(SWT.DEFAULT, BOTTOM_BAR_SIZE)
                .hint(SWT.DEFAULT, BOTTOM_BAR_SIZE).create());
        GridLayoutFactory.fillDefaults().extendedMargins(0, 0, 0, 3).spacing(0, 0).numColumns(1)
                .generateLayout(parent);
    }
    
    public Composite body() {
        return body;
    }
    
    public Composite bottomBar() {
        return bottomBar;
    }
    
}
