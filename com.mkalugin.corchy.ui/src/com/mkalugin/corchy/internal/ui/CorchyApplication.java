package com.mkalugin.corchy.internal.ui;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

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
