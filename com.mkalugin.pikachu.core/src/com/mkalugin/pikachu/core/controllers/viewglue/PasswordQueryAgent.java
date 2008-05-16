package com.mkalugin.pikachu.core.controllers.viewglue;

public interface PasswordQueryAgent {
    
    PasswordResult askPassword(String domain, String login);
    
}
