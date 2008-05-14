package com.mkalugin.corchy.ui.core;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import com.mkalugin.corchy.internal.ui.CorchyUserInterface;
import com.mkalugin.pikachu.core.Controller;
import com.mkalugin.pikachu.core.Document;

public class CorchyApplication implements IApplication {

	public Object start(IApplicationContext context) throws Exception {
		
		CorchyUserInterface ui = new CorchyUserInterface();
		Document document = new Document();
		Controller controller = new Controller(document, ui.createDocumentWindow());
		controller.run();
		ui.run();
		
		return EXIT_OK;
	}

	public void stop() {
	}

}
