package com.mkalugin.pikachu.core;

import java.io.File;

import com.mkalugin.pikachu.core.controllers.viewglue.DocumentBinding;

public class Document {
    
    private String content = "\n\nTyagayte hloptsi, I'll be back.\n";
    private final File file;
    private final boolean isUntitled;

    public Document(File file, boolean isUntitled) {
        this.file = file;
        this.isUntitled = isUntitled;
    }

    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public DocumentBinding getBinding() {
        return new DocumentBinding(file.toString(), file, isUntitled);
    }
    
}
