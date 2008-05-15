package com.mkalugin.corchy.ui.core;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class CorchyUIPlugin extends Plugin {

	private static CorchyUIPlugin instance;

	@Override
	public void start(BundleContext context) throws Exception {
	    super.start(context);
	    instance = this;
	}
	
	public static CorchyUIPlugin instance() {
		return instance;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
	    instance = null;
	    super.stop(context);
	}

}
