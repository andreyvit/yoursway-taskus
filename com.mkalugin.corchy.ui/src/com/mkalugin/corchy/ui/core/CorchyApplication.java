package com.mkalugin.corchy.ui.core;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import com.mkalugin.corchy.internal.ui.CorchyWindow;

public class CorchyApplication implements IApplication {

	public Object start(IApplicationContext context) throws Exception {
		CorchyWindow corchyWindow = new CorchyWindow();
		corchyWindow.setBlockOnOpen(true);
		corchyWindow.open();
		return EXIT_OK;
	}

	public void stop() {
	}

}
