package com.mkalugin.corchy.internal.ui;

import static com.mkalugin.corchy.internal.ui.Utils.lookup;

import org.eclipse.jface.dialogs.IDialogSettings;

public class DialogSettingsProvider {
    
    private final IDialogSettings parentStorage;

    public DialogSettingsProvider(IDialogSettings parentStorage) {
        if (parentStorage == null)
            throw new NullPointerException("parentStorage is null");
        this.parentStorage = parentStorage;
    }
    
    public IDialogSettings forKey(String key) {
        // TODO: remove old sections here
        return lookup(parentStorage, key);
    }
    
}
