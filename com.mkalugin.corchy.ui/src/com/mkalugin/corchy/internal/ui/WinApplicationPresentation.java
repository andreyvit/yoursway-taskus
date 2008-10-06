package com.mkalugin.corchy.internal.ui;

import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

import com.mkalugin.pikachu.core.controllers.viewglue.ApplicationPresentationCallback;
import com.mkalugin.pikachu.core.controllers.viewglue.DocumentWindow;
import com.mkalugin.pikachu.core.controllers.viewglue.DocumentWindowCallback;
import com.mkalugin.pikachu.core.preference.IPreferenceStore;

public class WinApplicationPresentation extends SwtCocoaApplicationPresentation {
    
    public WinApplicationPresentation(ApplicationPresentationCallback callback,
            IPreferenceStore preferenceStore) {
        
        super(callback, preferenceStore);
    }
    
    @Override
    protected boolean windowsDisposed() {
        return documentWindows.size() == 0;
    }
    
    @Override
    public DocumentWindow createDocumentWindow(DocumentWindowCallback callback) {
        SwtCocoaWindow window = super.createSwtCocoaWindow(callback);
        
        Shell shell = window.getShell();
        Menu menu = createMenuBar(shell);
        shell.setMenuBar(menu);
        
        return window;
    }
}
