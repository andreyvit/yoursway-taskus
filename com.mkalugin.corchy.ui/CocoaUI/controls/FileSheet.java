package com.mkalugin.corchy.ui.controls;

import static com.google.common.collect.Iterables.newArray;

import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.Callback;
import org.eclipse.swt.internal.cocoa.NSAlert;
import org.eclipse.swt.internal.cocoa.NSArray;
import org.eclipse.swt.internal.cocoa.NSOpenPanel;
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
    private String[] fileNames;
    
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

    protected NSSavePanel createPanel() {
        fileNames = new String [0];
        NSSavePanel panel;
        if ((getStyle() & SWT.SAVE) != 0) {
            NSSavePanel savePanel = NSSavePanel.savePanel();
            panel = savePanel;
        } else {
            NSOpenPanel openPanel = NSOpenPanel.openPanel();
            openPanel.setAllowsMultipleSelection((getStyle() & SWT.MULTI) != 0);
            panel = openPanel;
        }
        if (getFilterPath() != null) panel.setDirectory(NSString.stringWith(getFilterPath()));
        panel.setTitle(NSString.stringWith(getText() != null ? getText() : ""));
        return panel;
    }

    protected String interpretResult(NSSavePanel panel, int response) {
        String fullPath = null;
        if (response == OS.NSFileHandlingPanelOKButton) {
            NSString filename = panel.filename();
            char[] buffer = new char[filename.length()];
            filename.getCharacters_(buffer);
            fullPath = new String(buffer);
            if ((getStyle() & SWT.SAVE) == 0) {
                NSArray filenames = ((NSOpenPanel)panel).filenames();
                int count = filenames.count();
                fileNames = new String[count];
                for (int i = 0; i < count; i++) {
                    filename = new NSString(filenames.objectAtIndex(i));
                    buffer = new char[filename.length()];
                    filename.getCharacters_(buffer);
                    fileNames[i] = new String(buffer);
                }
            }
            setFilterIndex(-1);
        }
        return fullPath;
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
