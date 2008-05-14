package com.mkalugin.pikachu.core.controllers.viewglue;

import java.io.File;

public class DocumentBinding {
    
    private final String uniqueKey;
    private final File file;
    private final boolean isUntitled;

    public DocumentBinding(String uniqueKey, File file, boolean isUntitled) {
        this.uniqueKey = uniqueKey;
        this.file = file;
        this.isUntitled = isUntitled;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public File getFile() {
        return file;
    }

    public boolean isUntitled() {
        return isUntitled;
    }
    
}
