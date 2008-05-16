package com.mkalugin.pikachu.core.controllers.viewglue;

public class PasswordResult {
    
    private final String password;
    private final boolean storeInKeychain;

    public PasswordResult(String password, boolean storeInKeychain) {
        if (password == null)
            throw new NullPointerException("password is null");
        this.password = password;
        this.storeInKeychain = storeInKeychain;
    }
    
    public String getPassword() {
        return password;
    }
    
    public boolean shouldStoreInKeychain() {
        return storeInKeychain;
    }
    
}
