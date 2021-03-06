package com.mkalugin.corchy.ui.core;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import com.mkalugin.corchy.internal.ui.SwtCocoaApplicationPresentationFactory;
import com.mkalugin.pikachu.core.PikachuCore;
import com.mkalugin.pikachu.core.controllers.ApplicationController;
import com.mkalugin.pikachu.core.model.ApplicationFolders;
import com.mkalugin.pikachu.core.model.ApplicationModel;
import com.mkalugin.pikachu.core.model.OpenDocumentListPersister;
import com.mkalugin.pikachu.core.preference.IPreferenceStore;

public class CorchyApplication implements IApplication {
    
    public Object start(IApplicationContext context) throws Exception {
        IPreferenceStore preferences = PikachuCore.getPreferenceStore();
        ApplicationModel model = new ApplicationModel(ApplicationFolders.untitledDocumentsFolder());
        OpenDocumentListPersister persister = new OpenDocumentListPersister(model, preferences);
        ApplicationController controller = new ApplicationController(model, preferences,
                new SwtCocoaApplicationPresentationFactory(preferences));
        
        persister.openPreviouslyOpenedFiles();
        controller.run();
        return EXIT_OK;
    }
    
    public void stop() {
    }
    
}
