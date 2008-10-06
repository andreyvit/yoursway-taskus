package com.mkalugin.pikachu.core.controllers.passwords;

public interface PasswordStore {
    
    String passwordFor(String domain, String login);
    
    boolean canStore();
    
    void store(String domain, String login, String password);
    
}
