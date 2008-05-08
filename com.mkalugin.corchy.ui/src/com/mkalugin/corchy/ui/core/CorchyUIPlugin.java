package com.mkalugin.corchy.ui.core;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;


public class CorchyUIPlugin extends AbstractUIPlugin {

	private static CorchyUIPlugin instance; 
	
	public CorchyUIPlugin() {
		instance = this;	
	}

	public static CorchyUIPlugin instance() {
		return instance;
	}

}
