package com.mkalugin.pikachu.core.model;

import static com.google.common.base.Join.join;
import static com.google.common.collect.Lists.newArrayList;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.mkalugin.corchy.ui.core.preference.IPreferenceStore;

public class OpenDocumentListPersister implements ApplicationModelListener {
    
    private static final String FILES_KEY = "previouslyOpenedFiles";
    
    private final ApplicationModel model;
    private final IPreferenceStore store;

    public OpenDocumentListPersister(ApplicationModel model, IPreferenceStore store) {
        this.model = model;
        this.store = store;
        model.addListener(this);
    }
    
    public void openPreviouslyOpenedFiles() throws IOException {
        for (String name : store.getString(FILES_KEY).split("\n")) {
            File file = new File(name.trim());
            if (file.isFile())
                model.openExistingDocument(file);
        }
    }
    
    public void documentOpened(Document document) {
        saveDocumentList();
    }

    public void documentClosed(Document document) {
        saveDocumentList();
    }

    public void documentFileChanged(Document document) {
        // we save only named documents, and the document might have just become named
        saveDocumentList();
    }

    private void saveDocumentList() {
        List<String> pathes = newArrayList();
        for (Document document : model.getOpenDocuments())
            if (!document.isUntitled())
                pathes.add(document.getFile().getPath());
        store.setValue(FILES_KEY, join("\n", pathes));
    }
    
}
