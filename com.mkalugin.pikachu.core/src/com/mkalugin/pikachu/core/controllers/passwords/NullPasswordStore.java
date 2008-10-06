package com.mkalugin.pikachu.core.controllers.passwords;

public class NullPasswordStore implements PasswordStore {
    
    public String passwordFor(String domain, String login) {
        return null;
    }
    
    public boolean canStore() {
        return false;
    }
    
    public void store(String domain, String login, String password) {
        throw new UnsupportedOperationException();
    }
    
}
