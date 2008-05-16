package com.mkalugin.corchy.internal.ui;

import com.mkalugin.pikachu.core.controllers.viewglue.ApplicationPresentation;
import com.mkalugin.pikachu.core.controllers.viewglue.ApplicationPresentationCallback;
import com.mkalugin.pikachu.core.controllers.viewglue.ApplicationPresentationFactory;
import com.mkalugin.pikachu.core.preference.IPreferenceStore;

public class SwtCocoaApplicationPresentationFactory implements ApplicationPresentationFactory {
    
    private final IPreferenceStore preferenceStore;

    public SwtCocoaApplicationPresentationFactory(IPreferenceStore preferenceStore) {
        if (preferenceStore == null)
            throw new NullPointerException("preferenceStore is null");
        this.preferenceStore = preferenceStore;
    }

    public ApplicationPresentation createPresentation(ApplicationPresentationCallback callback) {
        return new SwtCocoaApplicationPresentation(callback, preferenceStore);
    }
    
}
