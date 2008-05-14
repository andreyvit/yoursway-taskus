package com.mkalugin.pikachu.core.controllers.viewglue;

import java.io.File;

public interface ApplicationPresentation extends DocumentWindowFactory {
    
    void run();

    File chooseDocumentToOpen(String defaultExtention);

    void displayError(String title, String message);
    
}
