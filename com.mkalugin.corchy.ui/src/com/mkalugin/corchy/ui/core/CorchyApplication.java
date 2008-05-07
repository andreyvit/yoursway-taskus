package com.mkalugin.corchy.ui.core;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.preference.IPreferenceStore;

import com.mkalugin.corchy.internal.ui.CorchyWindow;
import com.mkalugin.pikachu.core.storage.DataStorage;
import com.mkalugin.pikachu.core.storage.DataStorageBuilder;
import com.mkalugin.pikachu.core.storage.DataStorageManager;
import com.mkalugin.pikachu.core.storage.StorageException;
import com.mkalugin.pikachu.core.storage.memory.MemoryDataStorage;
import com.mkalugin.pikachu.core.workspace.Workspace;

public class CorchyApplication implements IApplication {

	private static Workspace workspace;

	public static Workspace workspace() {
		if (workspace == null)
			workspace = openLast();
		return workspace;
	}

	private static Workspace openLast() {
		IPreferenceStore preferenceStore = CorchyUIPlugin.instance().getPreferenceStore();
		String lastWorkspace = preferenceStore.getString("workspaceStorageType");
		String lastWorkspaceMemento = preferenceStore.getString("workspaceMemento");
		if (lastWorkspace != null && lastWorkspace.length() > 0) {
			DataStorageBuilder builder = DataStorageManager.getBuilderFor(lastWorkspace);
			if (builder != null) {
				DataStorage dataStorage = builder.buildFrom(lastWorkspaceMemento);
				return new Workspace(dataStorage);
			}
		}
		return openWorkspaceWithStorage(new MemoryDataStorage());
	}

	public static Workspace openWorkspaceWithStorage(DataStorage storage) {
		if (workspace != null) {
			try {
				workspace.flush();
			} catch (StorageException e) {
				e.printStackTrace();
			}
		}
		workspace = new Workspace(storage);
		saveWorkspaceState();
		return workspace;
	}
	
	public static void saveWorkspaceState() {
		IPreferenceStore preferenceStore = CorchyUIPlugin.instance().getPreferenceStore();
		preferenceStore.setValue("workspaceStorageType", workspace.storage().getType());
		preferenceStore.setValue("workspaceMemento", workspace.storage().getMemento());
	}

	public Object start(IApplicationContext context) throws Exception {
		CorchyWindow corchyWindow = new CorchyWindow();
		corchyWindow.setBlockOnOpen(true);
		corchyWindow.open();
		return EXIT_OK;
	}

	public void stop() {
	}

}
