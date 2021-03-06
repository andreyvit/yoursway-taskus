package com.mkalugin.corchy.internal.ui;

import static com.mkalugin.pikachu.core.PlatformSpecific.isWindows;

import org.eclipse.swt.widgets.Display;

import com.mkalugin.corchy.internal.ui.editor.DocumentStylesheet;
import com.mkalugin.corchy.internal.ui.editor.MacDocumentStylesheet;
import com.mkalugin.corchy.internal.ui.editor.WinDocumentStylesheet;
import com.mkalugin.pikachu.core.controllers.viewglue.ApplicationPresentation;
import com.mkalugin.pikachu.core.controllers.viewglue.ApplicationPresentationCallback;
import com.mkalugin.pikachu.core.preference.IPreferenceStore;

public class PlatformSpecific {
    
    public static ApplicationPresentation createApplicationPresentation(
            ApplicationPresentationCallback callback, IPreferenceStore preferenceStore) {
        
        if (isWindows())
            return new WinApplicationPresentation(callback, preferenceStore);
        else
            return new MacApplicationPresentation(callback, preferenceStore);
    }
    
    public static DocumentStylesheet createStylesheet(Display display) {
        if (isWindows())
            return new WinDocumentStylesheet(display);
        else
            return new MacDocumentStylesheet(display);
    }
    
}
