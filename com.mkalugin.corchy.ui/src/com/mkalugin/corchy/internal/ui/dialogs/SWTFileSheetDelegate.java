package com.mkalugin.corchy.internal.ui.dialogs;

import org.eclipse.swt.internal.cocoa.NSObject;
import org.eclipse.swt.internal.cocoa.OS;

public class SWTFileSheetDelegate extends NSObject {

    public SWTFileSheetDelegate() {
        super(0);
    }
        
    public SWTFileSheetDelegate(int id) {
        super(id);
    }

    public int tag() {
        int[] tag = new int[1];
        OS.object_getInstanceVariable(id, "tag", tag);    
        return tag[0];
    }

    public void setTag(int tag) {
        OS.object_setInstanceVariable(id, "tag", tag);
    }

   
}
