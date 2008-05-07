package com.mkalugin.pikachu.core.storage;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

public class DataStorageManager {

	private static final String EXT_POINT = "com.mkalugin.pikachu.core.dataStorages";

	private static Map<String, DataStorageBuilder> builders = new HashMap<String, DataStorageBuilder>();

	static {
		IConfigurationElement[] elements = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(EXT_POINT);
		for (IConfigurationElement e : elements) {
			try {
				String id = e.getAttribute("id");
				DataStorageBuilder b = (DataStorageBuilder) e.createExecutableExtension("class");
				builders.put(id, b);
			} catch (CoreException e1) {
				continue;
			}
		}
	}

	public static DataStorageBuilder getBuilderFor(String storageType) {
		return builders.get(storageType);
	}

}
