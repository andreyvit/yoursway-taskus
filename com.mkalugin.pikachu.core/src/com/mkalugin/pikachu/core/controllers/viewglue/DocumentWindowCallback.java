package com.mkalugin.pikachu.core.controllers.viewglue;

import com.mkalugin.pikachu.core.ast.AProjectName;

public interface DocumentWindowCallback {
    
    boolean closeFile();
    
    void startSynchronization(AProjectName projectName);

    void saveFileAs();
    
}
