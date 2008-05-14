package com.mkalugin.corchy.internal.ui;

import static com.google.common.collect.Iterables.newArray;

import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.Callback;
import org.eclipse.swt.internal.cocoa.NSAlert;
import org.eclipse.swt.internal.cocoa.NSSavePanel;
import org.eclipse.swt.internal.cocoa.NSString;
import org.eclipse.swt.internal.cocoa.OS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public abstract class FileSheet extends FileDialog {
    
    private static final int sel_savePanelDidEnd_returnCode_contextInfo_ = OS
            .sel_registerName("savePanelDidEnd:returnCode:contextInfo:");
    private NSSavePanel panel;
    
    private static void initClass() {
        Callback callback = new Callback(FileSheet.class, "delegateProc", 5);
        int proc = callback.getAddress();
        if (proc == 0)
            SWT.error(SWT.ERROR_NO_MORE_CALLBACKS);
        
        String className = "SWTFileSheetDelegate";
        int cls = OS.objc_allocateClassPair(OS.class_NSObject, className, 0);
        OS.class_addIvar(cls, "tag", OS.PTR_SIZEOF, (byte) (Math.log(OS.PTR_SIZEOF) / Math.log(2)), "i");
        OS.class_addMethod(cls, sel_savePanelDidEnd_returnCode_contextInfo_, proc, "@:@ii");
        OS.objc_registerClassPair(cls);
    }
    
    static {
        initClass();
    }
    
    public FileSheet(Shell parent, int style) {
        super(parent, style);
    }
    
    @Override
    protected void checkSubclass() {
    }
    
    @Override
    public String open() {
        panel = createPanel();
        SWTFileSheetDelegate delegate = (SWTFileSheetDelegate) new SWTFileSheetDelegate().alloc().init();
        delegate.setTag(OS.NewGlobalRef(this));
        panel.beginSheetForDirectory(NSString.stringWith(getFilterPath()),
                NSString.stringWith(getFileName()), 
                getParent().view.window(), 
                delegate, 
                sel_savePanelDidEnd_returnCode_contextInfo_, 0);
        return null;
    }
    
    static int delegateProc(int id, int sel, int arg0, int arg1, int arg2) {
        SWTFileSheetDelegate delegate = new SWTFileSheetDelegate(id);
        int ref = delegate.tag();
        FileSheet self = (FileSheet) OS.JNIGetObject(ref);
        OS.DeleteGlobalRef(ref);
        self.finished(arg1);
        return 0;
    }

    private void finished(int arg1) {
        finished(interpretResult(panel, arg1));
    }
    
    protected abstract void finished(String result);
    
}
