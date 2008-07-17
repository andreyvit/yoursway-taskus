package com.mkalugin.corchy.internal.ui.dialogs;

import org.eclipse.swt.widgets.Shell;

import com.mkalugin.corchy.ui.controls.BasicAlert;

public class GotItAlert extends BasicAlert {

    public GotItAlert(Shell parent) {
        super(parent);
        addButton("Got it");
    }

    @Override
    protected void finished(int button) {
    }
    
}
