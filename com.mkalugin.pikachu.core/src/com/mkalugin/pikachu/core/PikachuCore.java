package com.mkalugin.pikachu.core;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.Plugin;

import com.mkalugin.pikachu.core.model.ApplicationFolders;
import com.mkalugin.pikachu.core.preference.IPreferenceStore;
import com.mkalugin.pikachu.core.preference.IPropertyChangeListener;
import com.mkalugin.pikachu.core.preference.PreferenceStore;
import com.mkalugin.pikachu.core.preference.PropertyChangeEvent;

public class PikachuCore extends Plugin {
    
    private static PreferenceStore preferenceStore;
    
    public static IPreferenceStore getPreferenceStore() {
        if (preferenceStore == null) {
            preferenceStore = new PreferenceStore(new File(ApplicationFolders.applicationDataFolder(),
                    "Corchy.prefs").getAbsolutePath());
            try {
                preferenceStore.load();
            } catch (IOException e) {
            }
            preferenceStore.addPropertyChangeListener(new IPropertyChangeListener() {
                
                public void propertyChange(PropertyChangeEvent event) {
                    savePreferenceStore();
                }
                
            });
        }
        return preferenceStore;
    }
    
    static void savePreferenceStore() {
        try {
            preferenceStore.save();
        } catch (IOException e) {
        }
    }
    
}
