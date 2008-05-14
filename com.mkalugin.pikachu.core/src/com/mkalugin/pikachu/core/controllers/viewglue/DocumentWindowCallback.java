package com.mkalugin.pikachu.core.controllers.viewglue;

public interface DocumentWindowCallback {
    
    boolean closeFile();
    
    void startSynchronization();

    void saveFileAs();
    
}
