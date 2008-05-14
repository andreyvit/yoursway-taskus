package com.mkalugin.corchy.internal.ui;

import org.eclipse.jface.preference.IPreferenceStore;

public class DialogSettingsProvider {
    
    private final IPreferenceStore parentStorage;

    public DialogSettingsProvider(IPreferenceStore preferenceStore) {
        if (preferenceStore == null)
            throw new NullPointerException("parentStorage is null");
        this.parentStorage = preferenceStore;
    }
    
    public IPreferenceStore forKey(String key) {
        return new SubPreferenceStore(parentStorage, key);
    }
    
}
