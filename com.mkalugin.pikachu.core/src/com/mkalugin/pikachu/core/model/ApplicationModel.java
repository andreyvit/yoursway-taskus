package com.mkalugin.pikachu.core.model;

import static com.google.common.collect.Lists.newArrayList;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collection;

import com.mkalugin.pikachu.core.Document;

public class ApplicationModel {
    
    private static final String DOCUMENT_EXT = "corchy";
    private final File untitledDocumentsDir;

    public ApplicationModel(File untitledDocumentsDir) {
        if (untitledDocumentsDir == null)
            throw new NullPointerException("untitledDocumentsDir is null");
        this.untitledDocumentsDir = untitledDocumentsDir;
    }
    
    public Document createEmptyDocument() throws IOException {
        File file = chooseUntitledFileLocation();
        return new Document(file, true);
    }
    
    public Collection<Document> openAllUntitledDocuments() {
        Collection<Document> result = newArrayList();
        File[] files = untitledDocumentsDir.listFiles();
        if (files != null)
            for (File file : files)
                result.add(doOpenDocument(file, true));
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

    public String getDefaultDocumentExtension() {
        return DOCUMENT_EXT;
    }

    public Document openDocument(File file) {
        return doOpenDocument(file, false);
    }

    private Document doOpenDocument(File file, boolean isUntitled) {
        return new Document(file, isUntitled);
    }
    
}
