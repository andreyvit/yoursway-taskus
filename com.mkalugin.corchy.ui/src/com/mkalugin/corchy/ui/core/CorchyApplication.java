package com.mkalugin.corchy.ui.core;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;

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
		workspace.connectTo(storage);
		saveLastStorageInfo(storage);
		return workspace;
	}
	
	public static void saveLastStorageInfo(DataStorage storage) {
		IPreferenceStore preferenceStore = CorchyUIPlugin.instance().getPreferenceStore();
		preferenceStore.setValue("workspaceStorageType", storage.getType());
		preferenceStore.setValue("workspaceMemento", storage.getMemento());
	}
	
	public static void saveLastStorageInfo() {
		if (workspace != null)
			saveLastStorageInfo(workspace.storage());
	}

	public Object start(IApplicationContext context) throws Exception {
		Display.setAppName("Corchy");
		
		CorchyWindow corchyWindow = new CorchyWindow();
		corchyWindow.setBlockOnOpen(true);
		corchyWindow.open();
		return EXIT_OK;
	}

	public void stop() {
	}

}
