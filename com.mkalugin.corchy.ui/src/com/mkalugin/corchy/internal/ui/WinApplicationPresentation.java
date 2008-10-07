package com.mkalugin.corchy.internal.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

import com.mkalugin.corchy.internal.ui.util.MenuBuilder;
import com.mkalugin.pikachu.core.controllers.viewglue.ApplicationPresentationCallback;
import com.mkalugin.pikachu.core.controllers.viewglue.DocumentWindow;
import com.mkalugin.pikachu.core.controllers.viewglue.DocumentWindowCallback;
import com.mkalugin.pikachu.core.preference.IPreferenceStore;

public class WinApplicationPresentation extends SwtCocoaApplicationPresentation {
    
    private boolean quit = false;
    
    public WinApplicationPresentation(ApplicationPresentationCallback callback,
            IPreferenceStore preferenceStore) {
        
        super(callback, preferenceStore);
    }
    
    @Override
    protected boolean windowsDisposed() {
        return quit || documentWindows.size() == 0;
    }
    
    @Override
    public DocumentWindow createDocumentWindow(DocumentWindowCallback callback) {
        SwtCocoaWindow window = super.createSwtCocoaWindow(callback);
        
        Shell shell = window.getShell();
        Menu menu = createMenuBar(shell);
        shell.setMenuBar(menu);
        
        return window;
    }
    
    @Override
    protected Menu createFileMenu(Shell shell) {
        Menu menu = super.createFileMenu(shell);
        MenuBuilder builder = new MenuBuilder(menu);
        
        builder.separator();
        builder.item("E&xit", SWT.MOD1 | 'Q', new Runnable() {
            public void run() {
                quit = true;
            }
        });
        
        return menu;
    }
}
