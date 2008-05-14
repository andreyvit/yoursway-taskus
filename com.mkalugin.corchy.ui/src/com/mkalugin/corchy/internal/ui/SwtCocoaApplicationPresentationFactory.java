package com.mkalugin.corchy.internal.ui;

import com.mkalugin.corchy.ui.core.CorchyUIPlugin;
import com.mkalugin.pikachu.core.controllers.viewglue.ApplicationPresentation;
import com.mkalugin.pikachu.core.controllers.viewglue.ApplicationPresentationCallback;
import com.mkalugin.pikachu.core.controllers.viewglue.ApplicationPresentationFactory;

public class SwtCocoaApplicationPresentationFactory implements ApplicationPresentationFactory {

    public ApplicationPresentation createPresentation(ApplicationPresentationCallback callback) {
        return new SwtCocoaApplicationPresentation(callback, CorchyUIPlugin.instance().getDialogSettings());
    }
    
}
