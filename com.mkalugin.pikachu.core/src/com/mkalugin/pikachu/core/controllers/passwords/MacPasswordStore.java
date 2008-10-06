package com.mkalugin.pikachu.core.controllers.passwords;

import com.mkalugin.keychain.KeyChain;

public class MacPasswordStore implements PasswordStore {
    
    public String passwordFor(String domain, String login) {
        return KeyChain.GetPasswordFromKeychain(domain, login);
    }
    
    public boolean canStore() {
        return true;
    }
    
    public void store(String domain, String login, String password) {
        KeyChain.StorePasswordInKeychain(domain, login, password);
    }
    
}
