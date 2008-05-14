package com.mkalugin.corchy.ui.core;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import com.mkalugin.corchy.internal.ui.SwtCocoaApplicationPresentationFactory;
import com.mkalugin.pikachu.core.controllers.ApplicationController;
import com.mkalugin.pikachu.core.model.ApplicationModel;

public class CorchyApplication implements IApplication {
    
    public Object start(IApplicationContext context) throws Exception {
        new ApplicationController(new ApplicationModel(), new SwtCocoaApplicationPresentationFactory()).run();
        return EXIT_OK;
    }
    
    public void stop() {
    }
    
}
