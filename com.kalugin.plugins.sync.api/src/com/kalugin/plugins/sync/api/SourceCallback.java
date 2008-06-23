package com.kalugin.plugins.sync.api;

public interface SourceCallback {
    
    String askPassword(String domain, String login, boolean forceQuery);
    
    void setProgressMessage(String message);
    
    void abortWithMessage(String message);
    
}
