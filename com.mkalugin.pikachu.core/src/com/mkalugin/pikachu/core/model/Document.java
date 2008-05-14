package com.mkalugin.pikachu.core.model;

import static com.yoursway.utils.Listeners.newListenersByIdentity;
import static com.yoursway.utils.YsFileUtils.readAsString;
import static com.yoursway.utils.YsFileUtils.writeString;

import java.io.File;
import java.io.IOException;

import com.mkalugin.pikachu.core.DocumentListener;
import com.mkalugin.pikachu.core.controllers.viewglue.DocumentBinding;
import com.yoursway.utils.Listeners;

public class Document {
    
    private String content = "\n\nTyagayte hloptsi, I'll be back.\n";
    private File file;
    private final boolean isUntitled;
    private final DocumentOwner owner;

    public Document(DocumentOwner owner, File file, boolean isUntitled) throws IOException {
        if (owner == null)
            throw new NullPointerException("owner is null");
        if (file == null)
            throw new NullPointerException("file is null");
        this.owner = owner;
        this.file = file;
        this.isUntitled = isUntitled;
        loadContent();
    }
    
    private transient Listeners<DocumentListener> listeners = newListenersByIdentity();
    
    public synchronized void addListener(DocumentListener listener) {
        listeners.add(listener);
    }
    
    public synchronized void removeListener(DocumentListener listener) {
        listeners.remove(listener);
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
    
    public boolean isUntitled() {
        return isUntitled;
    }
    
    public DocumentTypeDefinition documentTypeDefinition() {
        return owner.documentTypeDefinition();
    }
    
    private void loadContent() throws IOException {
        content = readAsString(file);
    }
    
    private void saveContent(File file) throws IOException {
        writeString(file, content);
    }
    
    public void saveAs(File file) throws IOException {
        try {
            saveContent(file);
            this.file = file;
        } catch (IOException e) {
            // if writing failed, delete the partially written file
            if (file.exists())
                file.delete();
            throw e;
        }
    }
    
}
