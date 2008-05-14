package com.mkalugin.pikachu.core;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.Plugin;

import com.mkalugin.corchy.ui.core.preference.IPreferenceStore;
import com.mkalugin.corchy.ui.core.preference.IPropertyChangeListener;
import com.mkalugin.corchy.ui.core.preference.PreferenceStore;
import com.mkalugin.corchy.ui.core.preference.PropertyChangeEvent;
import com.mkalugin.pikachu.core.model.ApplicationFolders;

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
