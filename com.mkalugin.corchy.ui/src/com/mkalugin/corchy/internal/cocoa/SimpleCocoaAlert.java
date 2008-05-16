package com.mkalugin.corchy.internal.cocoa;

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
