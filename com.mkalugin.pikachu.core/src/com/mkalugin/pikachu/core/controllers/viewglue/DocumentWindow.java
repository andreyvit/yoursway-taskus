package com.mkalugin.pikachu.core.controllers.viewglue;

import java.io.File;

import com.mkalugin.pikachu.core.controllers.search.SearchControlsFactory;
import com.mkalugin.pikachu.core.model.DocumentTypeDefinition;

public interface DocumentWindow extends OutlineViewFactory, SourceViewFactory, SearchControlsFactory {
    
    void setText(String text);
    
    void openWindow();

    void setDocumentBinding(DocumentBinding documentBinding, boolean isDocumentEmpty);

    void askSaveDiscardCancel(SaveDiscardCancel handler);

    void close();

    void chooseFileNameToSaveInto(DocumentBinding binding, DocumentTypeDefinition documentTypeDefinition, FileNameRequestor requestor);

    void reportSavingFailed(File file);

	void openSynchProgressSheet();

	void closeSynchProgressSheet();
    
}
