package com.mkalugin.pikachu.core.controllers.passwords;

import com.mkalugin.pikachu.core.controllers.SyncronizationCancelledException;
import com.mkalugin.pikachu.core.controllers.viewglue.DocumentWindow;
import com.mkalugin.pikachu.core.controllers.viewglue.PasswordQueryAgent;
import com.mkalugin.pikachu.core.controllers.viewglue.PasswordResult;
import com.yoursway.utils.annotations.Nullable;

public class PasswordQueryController {
    
    private final PasswordQueryAgent agent;
    private final PasswordStore store;
    
    public PasswordQueryController(DocumentWindow window, @Nullable PasswordStore store) {
        agent = window.bindPasswordQueryAgent();
        
        if (store != null)
            this.store = store;
        else
            this.store = new NullPasswordStore();
    }
    
    public String askPassword(String domain, String login, boolean forceQuery) {
        String password = null;
        
        if (!forceQuery)
            password = store.passwordFor(domain, login);
        
        if (password == null) {
            PasswordResult result = agent.askPassword(domain, login, store.canStore());
            if (result == null)
                password = null;
            else {
                password = result.getPassword();
                if (result.shouldStoreInKeychain())
                    store.store(domain, login, password);
            }
        }
        
        if (password == null)
            throw new SyncronizationCancelledException();
        return password;
    }
}
