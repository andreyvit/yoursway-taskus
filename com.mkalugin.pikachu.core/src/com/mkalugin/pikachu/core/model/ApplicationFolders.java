package com.mkalugin.pikachu.core.model;

import java.io.File;
import java.io.IOException;

public class ApplicationFolders {
    
    private static final String APPLICATION_NAME = "Corchy";
    
    public static File applicationDataFolder() {
        try {
            return new File("~/Library/Application Data/" + APPLICATION_NAME).getCanonicalFile();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
    
    public static File untitledDocumentsFolder() {
        return new File(applicationDataFolder(), "Untitled Documents");
    }
    
}
