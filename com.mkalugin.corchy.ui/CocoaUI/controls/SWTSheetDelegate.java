package com.mkalugin.corchy.ui.controls;

import org.eclipse.swt.internal.cocoa.NSObject;
import org.eclipse.swt.internal.cocoa.OS;

public class SWTSheetDelegate extends NSObject {

    public SWTSheetDelegate() {
        super(0);
    }
        
    public SWTSheetDelegate(int id) {
        super(id);
    }

    public int tag() {
        int[] tag = new int[1];
        OS.object_getInstanceVariable(id, "tag", tag);    
        return tag[0];
//        return OS.objc_msgSend(id, OS.sel_tag);
    }

    public void setTag(int tag) {
        OS.object_setInstanceVariable(id, "tag", tag);
    }

   
}
