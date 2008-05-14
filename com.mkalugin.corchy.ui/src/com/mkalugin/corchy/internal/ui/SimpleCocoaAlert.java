package com.mkalugin.corchy.internal.ui;

import org.eclipse.swt.widgets.Shell;

public class SimpleCocoaAlert extends CocoaAlert {

    public SimpleCocoaAlert(Shell parent) {
        super(parent);
        addButton("Got it");
    }

    @Override
    protected void finished(int button) {
    }
    
}
