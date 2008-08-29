package com.mkalugin.corchy.ui.core;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class CorchyUIPlugin implements BundleActivator {

	private static CorchyUIPlugin instance;
	private BundleContext context;

	public void start(BundleContext context) throws Exception {
	    this.context = context;
		instance = this;
	}
	
	public static CorchyUIPlugin instance() {
		return instance;
	}

	public void stop(BundleContext context) throws Exception {
	    instance = null;
	}

	public Bundle getBundle() {
		return context.getBundle();
	}

}
