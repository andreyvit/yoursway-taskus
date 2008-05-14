package com.mkalugin.corchy.internal.ui;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class BottomBarComposition {
    
    private Composite body;
    private BottomBar bottomBar;
    
    public BottomBarComposition(Composite parent) {
        body = new Composite(parent, SWT.NONE);
        body.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        
        bottomBar = new BottomBar(parent);
        bottomBar.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).minSize(SWT.DEFAULT, 32)
                .hint(SWT.DEFAULT, 32).create());
        GridLayoutFactory.fillDefaults().extendedMargins(0, 0, 0, 0).spacing(0, 0).numColumns(1)
                .generateLayout(parent);
    }
    
    public Composite body() {
        return body;
    }
    
    public BottomBar bottomBar() {
        return bottomBar;
    }
    
}
