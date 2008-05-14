package com.mkalugin.pikachu.core.model;

import static com.google.common.collect.Lists.newArrayList;
import static com.yoursway.utils.Listeners.newListenersByIdentity;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collection;

import com.yoursway.utils.Listeners;

public class ApplicationModel implements DocumentOwner {
    
    private static final String DOCUMENT_EXT = "corchy";
    
    private final File untitledDocumentsDir;
    
    private Collection<Document> openDocuments = newArrayList();

    public ApplicationModel(File untitledDocumentsDir) {
        if (untitledDocumentsDir == null)
            throw new NullPointerException("untitledDocumentsDir is null");
        this.untitledDocumentsDir = untitledDocumentsDir;
    }
    
    private transient Listeners<ApplicationModelListener> listeners = newListenersByIdentity();
    
    public synchronized void addListener(ApplicationModelListener listener) {
        listeners.add(listener);
    }
    
    public synchronized void removeListener(ApplicationModelListener listener) {
        listeners.remove(listener);
    }
    
    
    public Document createEmptyDocument() throws IOException {
        File file = chooseUntitledFileLocation();
        return createDocument(file, true);
    }
    
    public void openAllUntitledDocuments() {
        for (Document document : loadAllUntitledDocuments())
            openDocument(document);
    }
    
    public Collection<Document> loadAllUntitledDocuments() {
        Collection<Document> result = newArrayList();
        File[] files = untitledDocumentsDir.listFiles();
        if (files != null)
            for (File file : files)
                try {
                    result.add(createDocument(file, true));
                } catch (IOException e) {
                    // reading error? hm, weird
                    e.printStackTrace(System.err);
                }
        return result;
    }

    private File chooseUntitledFileLocation() throws IOException {
        int i = 1;
        while (true) {
            File file = new File(untitledDocumentsDir, ("Untitled" + (i > 1 ? " " + i : "") + "." + DOCUMENT_EXT));
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                RandomAccessFile raf = new RandomAccessFile(file, "rw");
                raf.close();
                return file;
            }
            i++;
        }
    }

    public DocumentTypeDefinition documentTypeDefinition() {
        return new DocumentTypeDefinition(DOCUMENT_EXT);
    }

    public Document loadDocument(File file) throws IOException {
        return createDocument(file, false);
    }

    private Document createDocument(File file, boolean isUntitled) throws IOException {
        return new Document(this, file, isUntitled);
    }
    
    public void openNewDocument() throws IOException {
        openDocument(createEmptyDocument());
    }
    
    public void openExistingDocument(File file) throws IOException {
        openDocument(loadDocument(file));
    }
    
    public void openDocument(Document document) {
        openDocuments.add(document);
        for(ApplicationModelListener listener : listeners)
            listener.documentOpened(document);
    }

    public void documentClosed(Document document) {
        openDocuments.remove(document);
        for(ApplicationModelListener listener : listeners)
            listener.documentClosed(document);
    }
    
    public void documentFileChanged(Document document) {
        for(ApplicationModelListener listener : listeners)
            listener.documentFileChanged(document);
    }
    
    public Collection<Document> getOpenDocuments() {
        return openDocuments;
    }
    
    public boolean areNoDocumentsOpen() {
        return openDocuments.isEmpty();
    }
    
}
