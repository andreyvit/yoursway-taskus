package com.mkalugin.corchy.internal.ui;

import static com.mkalugin.corchy.ui.controls.PlatformStuff.setApplicationMenuBar;

import org.eclipse.swt.widgets.Shell;

import com.mkalugin.pikachu.core.controllers.viewglue.ApplicationPresentationCallback;
import com.mkalugin.pikachu.core.preference.IPreferenceStore;

public class MacApplicationPresentation extends SwtCocoaApplicationPresentation {
    
    private final Shell hiddenShell;
    
    public MacApplicationPresentation(ApplicationPresentationCallback callback,
            IPreferenceStore preferenceStore) {
        
        super(callback, preferenceStore);
        
        hiddenShell = new Shell();
        setApplicationMenuBar(display, createMenuBar(hiddenShell));
    }
    
    @Override
    protected boolean windowsDisposed() {
        return hiddenShell.isDisposed();
    }
    
}
