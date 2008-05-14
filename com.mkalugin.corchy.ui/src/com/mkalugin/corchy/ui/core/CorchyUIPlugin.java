package com.mkalugin.corchy.ui.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.osgi.framework.BundleContext;

public class CorchyUIPlugin extends Plugin {

	private static final String DIALOG_SETTINGS_FILE = "dialog_settings.xml"; //$NON-NLS-1$

	private static CorchyUIPlugin instance;

	private DialogSettings dialogSettings = null;

	private PreferenceStore preferenceStore;
	
	@Override
	public void start(BundleContext context) throws Exception {
	    super.start(context);
	    instance = this;
	}
	
	public static CorchyUIPlugin instance() {
		return instance;
	}

	protected File preferencesDir() {
		return new File(System.getProperty("user.home"), "Library/Preferences/Corchy.app/");
	}

	public IPreferenceStore getPreferenceStore() {
		if (preferenceStore == null) {
			preferenceStore = new PreferenceStore(
					new File(preferencesDir(), "CorchyUIPlugin.prefs").getAbsolutePath());
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

	public void savePreferenceStore() {
		try {
			if (preferenceStore != null)
				preferenceStore.save();
		} catch (IOException e) {
		}
	}

	@Override
	public void stop(BundleContext context) throws Exception {
	    instance = null;
	    super.stop(context);
	}

}
