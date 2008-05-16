package com.mkalugin.corchy.ui.core;

import com.mkalugin.pikachu.core.preference.IPreferenceStore;
import com.mkalugin.pikachu.core.preference.SubPreferenceStore;

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
