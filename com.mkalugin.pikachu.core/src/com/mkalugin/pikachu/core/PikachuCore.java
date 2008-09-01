package com.mkalugin.pikachu.core;

import java.io.File;
import java.io.IOException;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.mkalugin.pikachu.core.model.ApplicationFolders;
import com.mkalugin.pikachu.core.preference.IPreferenceStore;
import com.mkalugin.pikachu.core.preference.IPropertyChangeListener;
import com.mkalugin.pikachu.core.preference.PreferenceStore;
import com.mkalugin.pikachu.core.preference.PropertyChangeEvent;

public class PikachuCore implements BundleActivator {
    
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

	public void start(BundleContext context) throws Exception {
	}

	public void stop(BundleContext context) throws Exception {
	}
    
}
