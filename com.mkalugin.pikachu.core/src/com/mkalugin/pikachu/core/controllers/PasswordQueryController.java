package com.mkalugin.pikachu.core.controllers;

import com.kalugin.plugins.sync.api.SourceCallback;
import com.mkalugin.keychain.KeyChain;
import com.mkalugin.pikachu.core.controllers.viewglue.DocumentWindow;
import com.mkalugin.pikachu.core.controllers.viewglue.PasswordQueryAgent;
import com.mkalugin.pikachu.core.controllers.viewglue.PasswordResult;

public class PasswordQueryController {
    
    private PasswordQueryAgent agent;
    
    public PasswordQueryController(DocumentWindow window) {
        agent = window.bindPasswordQueryAgent();
    }
    
    public String askPassword(String domain, String login, boolean forceQuery) {
        String password = KeyChain.GetPasswordFromKeychain(domain, login);
        if (password == null || forceQuery) {
            PasswordResult result = agent.askPassword(domain, login);
            if (result == null)
                password = null;
            else {
                password = result.getPassword();
                if (result.shouldStoreInKeychain())
                    KeyChain.StorePasswordInKeychain(domain, login, password);
            }
        }
        if (password == null)
            throw new SyncronizationCancelledException();
        return password;
    }
    
}
