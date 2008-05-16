package com.mkalugin.pikachu.core.controllers;

import com.kalugin.plugins.sync.api.SourceCallback;
import com.mkalugin.keychain.KeyChain;
import com.mkalugin.pikachu.core.controllers.viewglue.DocumentWindow;
import com.mkalugin.pikachu.core.controllers.viewglue.PasswordQueryAgent;

public class PasswordQueryController implements SourceCallback {

    private PasswordQueryAgent agent;

    public PasswordQueryController(DocumentWindow window) {
        agent = window.bindPasswordQueryAgent();
    }

    public String askPassword(String domain, String login, boolean forceQuery) {
        String password = KeyChain.GetPasswordFromKeychain(domain, login);
        if (password == null || forceQuery) {
            password = agent.askPassword(domain, login);
            if (password != null)
                KeyChain.StorePasswordInKeychain(domain, login, password);
        }
        return password;
    }
    
}
