package com.mkalugin.pikachu.core.tests;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class Activator extends Plugin {
    
    private static Activator INSTANCE;
    
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        INSTANCE = this;
    }
    
    @Override
    public void stop(BundleContext context) throws Exception {
        INSTANCE = null;
        super.stop(context);
    }
    
    public static Activator getDefault() {
        return INSTANCE;
    }
    
}
