package com.mkalugin.corchy.internal.ui;

import org.eclipse.jface.dialogs.IDialogSettings;

public class Utils {

    public static IDialogSettings lookup(IDialogSettings parent, String sectionName) {
        IDialogSettings result = parent.getSection(sectionName);
        if (result == null)
            result = parent.addNewSection(sectionName);
        return result;
    }
    
}
