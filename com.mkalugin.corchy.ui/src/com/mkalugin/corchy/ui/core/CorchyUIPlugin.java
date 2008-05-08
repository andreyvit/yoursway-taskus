package com.mkalugin.corchy.ui.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class CorchyUIPlugin extends AbstractUIPlugin {

	private static final String DIALOG_SETTINGS_FILE = "dialog_settings.xml"; //$NON-NLS-1$

	private static CorchyUIPlugin instance;

	private DialogSettings dialogSettings = null;

	private PreferenceStore preferenceStore;

	public CorchyUIPlugin() {
		instance = this;
	}

	public static CorchyUIPlugin instance() {
		return instance;
	}

	public IDialogSettings getDialogSettings() {
		if (dialogSettings == null) {
			loadDialogSettings();
		}
		return dialogSettings;
	}

	protected File preferencesDir() {
		return new File(System.getProperty("user.home"), "Library/Preferences/Corchy.app/");
	}

	protected void loadDialogSettings() {
		dialogSettings = new DialogSettings("CorchyUIPlugin"); //$NON-NLS-1$

		FileReader in = null;
		try {
			in = new FileReader(new File(preferencesDir(), DIALOG_SETTINGS_FILE));
			BufferedReader reader = new BufferedReader(in);
			dialogSettings.load(reader);
		} catch (IOException e) {
			// load failed so ensure we have an empty settings
			dialogSettings = new DialogSettings("CorchyUIPlugin"); //$NON-NLS-1$
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	protected void saveDialogSettings() {
		if (dialogSettings == null) {
			return;
		}

		try {
			File prefs = preferencesDir();
			if (!prefs.exists())
				prefs.mkdirs();
			dialogSettings.save(new File(prefs, DIALOG_SETTINGS_FILE).getAbsolutePath());
		} catch (IOException e) {
			// spec'ed to ignore problems
		} catch (IllegalStateException e) {
			// spec'ed to ignore problems
		}
	}

	@Override
	public IPreferenceStore getPreferenceStore() {
		if (preferenceStore == null) {
			preferenceStore = new PreferenceStore(
					new File(preferencesDir(), "CorchyUIPlugin.prefs").getAbsolutePath());
			try {
				preferenceStore.load();
			} catch (IOException e) {
				return new PreferenceStore();
			}
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
		try {
			savePreferenceStore();
		} finally {
			super.stop(context);
		}
	}

}
