package com.mkalugin.corchy.internal.ui;
import org.eclipse.core.runtime.Plugin;


public class CorchyPlugin extends Plugin {

	private static CorchyPlugin instance; 
	
	public CorchyPlugin() {
		instance = this;
	}

	public static Plugin instance() {
		return instance;
	}

}
