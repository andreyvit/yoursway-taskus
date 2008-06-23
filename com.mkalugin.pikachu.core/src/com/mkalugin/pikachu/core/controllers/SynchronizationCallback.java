package com.mkalugin.pikachu.core.controllers;

import com.kalugin.plugins.sync.api.SourceCallback;
import com.mkalugin.pikachu.core.controllers.viewglue.DocumentWindow;

public class SynchronizationCallback implements SourceCallback {

	private PasswordQueryController passwordQueryController;
	private final DocumentWindow window;

	public SynchronizationCallback(DocumentWindow window) {
		this.window = window;
		passwordQueryController = new PasswordQueryController(window);
	}
	
	public String askPassword(String domain, String login, boolean forceQuery) {
		return passwordQueryController.askPassword(domain, login, forceQuery);
	}

	public void abortWithMessage(String message) {
		window.closeSynchProgressSheet();
		window.showAlertWithMessage("Synchronization failed", message);
	}

	public void setProgressMessage(String message) {
		window.setSynchProgressMessage(message);
	}

}
