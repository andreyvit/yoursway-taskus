package com.mkalugin.pikachu.core.controllers.viewglue;

public interface DocumentWindowCallback {
    
    void startSynchronization();
    
    String uniqueDocumentKeyForPreferencePersistance();
    
}
