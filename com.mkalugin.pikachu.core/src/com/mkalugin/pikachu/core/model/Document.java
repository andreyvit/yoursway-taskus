package com.mkalugin.pikachu.core.model;

import static com.mkalugin.pikachu.core.model.ApplicationFolders.synchronizationStateFolder;
import static com.yoursway.utils.Listeners.newListenersByIdentity;
import static com.yoursway.utils.YsDigest.sha1;
import static com.yoursway.utils.YsFileUtils.readAsString;
import static com.yoursway.utils.YsFileUtils.writeString;

import java.io.File;
import java.io.IOException;

import com.mkalugin.pikachu.core.DocumentListener;
import com.mkalugin.pikachu.core.ast.ADocument;
import com.mkalugin.pikachu.core.controllers.viewglue.DocumentBinding;
import com.mkalugin.pikachu.core.model.document.DocumentContent;
import com.mkalugin.pikachu.core.workspace.DocumentParser;
import com.yoursway.utils.Listeners;

public class Document {
    
    private String content;
    private DocumentContent contentModel;
    @Deprecated
    private ADocument ast;
    
    private File file;
    private boolean isEmpty;
    private boolean isUntitled;
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
    
    private transient final Listeners<DocumentListener> listeners = newListenersByIdentity();
    
    public synchronized void addListener(DocumentListener listener) {
        listeners.add(listener);
    }
    
    public synchronized void removeListener(DocumentListener listener) {
        listeners.remove(listener);
    }
    
    public synchronized String getContent() {
        return content;
    }
    
    public synchronized DocumentContent getContentModel() {
        return contentModel;
    }
    
    @Deprecated
    public synchronized ADocument getDocumentNode() {
        return ast;
    }
    
    public synchronized void setContent(String content, Object sender) {
        if (content == null)
            throw new NullPointerException("content is null");
        if (content.equals(this.content))
            return;
        this.content = content;
        this.contentModel = DocumentParser.parse(content);
        this.ast = new DocumentParser().parse_old(content);
        for (DocumentListener listener : listeners)
            listener.contentChanged(sender);
        boolean isEmpty = calculateIsEmpty();
        if (this.isEmpty != isEmpty) {
            this.isEmpty = isEmpty;
            for (DocumentListener listener : listeners)
                listener.emptinessChanged();
        }
    }
    
    private boolean calculateIsEmpty() {
        return content.trim().length() == 0;
    }
    
    public synchronized DocumentBinding getBinding() {
        return new DocumentBinding(file.toString(), file, isUntitled);
    }
    
    public synchronized boolean isUntitled() {
        return isUntitled;
    }
    
    public DocumentTypeDefinition documentTypeDefinition() {
        return owner.documentTypeDefinition();
    }
    
    private synchronized void loadContent() throws IOException {
        setContent(readAsString(file), this);
    }
    
    private synchronized void saveContent(File file) throws IOException {
        writeString(file, content);
    }
    
    public synchronized void saveAs(File file) throws IOException {
        try {
            saveContent(file);
            if (isUntitled)
                this.file.delete();
            this.file = file;
            isUntitled = false;
        } catch (IOException e) {
            // if writing failed, delete the partially written file
            if (file.exists())
                file.delete();
            throw e;
        }
        owner.documentFileChanged(this);
        for (DocumentListener listener : listeners)
            listener.bindingChanged();
    }
    
    public synchronized void save() throws IOException {
        saveContent(file);
    }
    
    public void discard() {
        if (isUntitled)
            file.delete();
        fireClosed(true);
    }
    
    public void close() {
        fireClosed(false);
    }
    
    private void fireClosed(boolean discarded) {
        owner.documentClosed(this);
        for (DocumentListener listener : listeners)
            listener.closed(discarded);
    }
    
    public File getFile() {
        return file;
    }
    
    public boolean isEmpty() {
        return isEmpty;
    }
    
    public File fuflaMufla() {
        try {
            return new File(synchronizationStateFolder(), sha1(file.getCanonicalPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
}
