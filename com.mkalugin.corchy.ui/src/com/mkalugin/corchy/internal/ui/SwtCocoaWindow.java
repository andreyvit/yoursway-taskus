package com.mkalugin.corchy.internal.ui;

import static com.mkalugin.corchy.internal.images.CorchyImages.ICN_SYNC;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.Callback;
import org.eclipse.swt.internal.cocoa.NSAlert;
import org.eclipse.swt.internal.cocoa.NSButton;
import org.eclipse.swt.internal.cocoa.NSString;
import org.eclipse.swt.internal.cocoa.OS;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.mkalugin.corchy.internal.editor.SwtCocoaSourceView;
import com.mkalugin.corchy.internal.ui.location.InitialShellPosition;
import com.mkalugin.corchy.internal.ui.location.WindowLocationConfiguration;
import com.mkalugin.corchy.internal.ui.location.WindowLocationManager;
import com.mkalugin.pikachu.core.ast.ADocument;
import com.mkalugin.pikachu.core.controllers.viewglue.DocumentBinding;
import com.mkalugin.pikachu.core.controllers.viewglue.DocumentWindow;
import com.mkalugin.pikachu.core.controllers.viewglue.DocumentWindowCallback;
import com.mkalugin.pikachu.core.controllers.viewglue.OutlineView;
import com.mkalugin.pikachu.core.controllers.viewglue.OutlineViewCallback;
import com.mkalugin.pikachu.core.controllers.viewglue.SaveDiscardCancel;
import com.mkalugin.pikachu.core.controllers.viewglue.SourceView;
import com.mkalugin.pikachu.core.controllers.viewglue.SourceViewCallback;

public class SwtCocoaWindow implements DocumentWindow {

    private static final String DIALOG_ID = "mainWindow";
    
    private static final String APP_TITLE = "Corchy";
    
    private static final String EXT = ".corchy";
    
    private SwtCocoaSourceView sourceView;
    private SwtCocoaOutlineView outlineView;
    private String title = null;
    
    private DocumentWindowCallback callback;
    
    private Shell shell;
    
    private final DialogSettingsProvider preferenceStorageProvider;
    
    private WindowLocationManager locationManager;
    
    public SwtCocoaWindow(Display display, DialogSettingsProvider preferenceStorageProvider,
            DocumentWindowCallback callback, InitialShellPosition initialPosition) {
        if (display == null)
            throw new NullPointerException("display is null");
        if (preferenceStorageProvider == null)
            throw new NullPointerException("preferenceStorageProvider is null");
        if (callback == null)
            throw new NullPointerException("callback is null");
        this.callback = callback;
        this.preferenceStorageProvider = preferenceStorageProvider;
        
        shell = new Shell();
        shell.setData(this);
        shell.setText(computeTitle());
        
        shell.addShellListener(new ShellAdapter() {
            
            @Override
            public void shellClosed(ShellEvent e) {
                e.doit = SwtCocoaWindow.this.callback.closeFile();
            }
            
        });
        
        BottomBarComposition composition = new BottomBarComposition(shell);
        createControls(composition.body());
        fillBottomBar(composition.bottomBar());
        
        locationManager = new WindowLocationManager(shell, new WindowLocationConfiguration().initialPosition(
                initialPosition).size(500, 600));
    }
    
    public void setDocumentBinding(DocumentBinding documentBinding) {
        locationManager.setDialogSettings(preferenceStorageProvider.forKey(documentBinding.getUniqueKey()));
        shell.setText(documentBinding.getFile().getName());
        shell.view.window().setRepresentedFilename(NSString.stringWith(documentBinding.getFile().getPath()));
        shell.view.window().setDocumentEdited(documentBinding.isUntitled());
    }
    
    public Shell getShell() {
        return shell;
    }
    
    private String computeTitle() {
        if (title != null)
            return title;
        return APP_TITLE;
    }
    
    private void createEditorAndOutline(final Composite parent) {
        outlineView = new SwtCocoaOutlineView(parent);
        sourceView = new SwtCocoaSourceView(parent);
        final Sash sash = new Sash(parent, SWT.VERTICAL);
        
        final FormLayout form = new FormLayout();
        form.spacing = 0;
        parent.setLayout(form);
        
        FormData outlineData = new FormData();
        outlineData.left = new FormAttachment(0, 0);
        outlineData.right = new FormAttachment(sash, 0);
        outlineData.top = new FormAttachment(0, 0);
        outlineData.bottom = new FormAttachment(100, 0);
        outlineView.setLayoutData(outlineData);
        
        // min 100 px. for the outline,
        // 30% initially
        final int limit = 100, percent = 30;
        final FormData sashData = new FormData();
        sashData.left = new FormAttachment(percent, 0);
        sashData.top = new FormAttachment(0, 0);
        sashData.bottom = new FormAttachment(100, 0);
        sashData.width = 2;
        sash.setLayoutData(sashData);
        sash.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                Rectangle sashRect = sash.getBounds();
                Rectangle shellRect = parent.getClientArea();
                int right = shellRect.width - sashRect.width - limit;
                e.x = Math.max(Math.min(e.x, right), limit);
                if (e.x != sashRect.x) {
                    sashData.left = new FormAttachment(0, e.x);
                    parent.layout();
                }
            }
        });
        
        FormData editorData = new FormData();
        editorData.left = new FormAttachment(sash, 0);
        editorData.right = new FormAttachment(100, 0);
        editorData.top = new FormAttachment(0, 0);
        editorData.bottom = new FormAttachment(100, 0);
        sourceView.setLayoutData(editorData);
    }
    
    private void createControls(Composite parent) {
        Composite editorBlock = new Composite(parent, SWT.NULL);
        createEditorAndOutline(editorBlock);
        editorBlock.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true)
                .create());
    }
    
    private void fillBottomBar(Composite bottomBar) {
        // Sync button
        Button syncButton = new Button(bottomBar, SWT.NONE | SWT.PUSH);
        ((NSButton) syncButton.view).setBezelStyle(OS.NSTexturedRoundedBezelStyle);
        ((NSButton) syncButton.view).setImagePosition(OS.NSImageOnly);
        syncButton.setImage(ICN_SYNC.get());
        syncButton.setLayoutData(GridDataFactory.defaultsFor(syncButton).align(SWT.BEGINNING, SWT.BEGINNING)
                .indent(0, 0).create());
        syncButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                callback.startSynchronization();
            }
        });
        
        // Search field
        Text searchField = new Text(bottomBar, SWT.SINGLE | SWT.SEARCH);
        searchField.setText("Search");
        searchField.setLayoutData(GridDataFactory.defaultsFor(searchField).align(SWT.END, SWT.BEGINNING)
                .indent(0, 0).create());
        
        GridLayoutFactory.fillDefaults().numColumns(2).extendedMargins(8, 8, 4, 0).margins(0, 0)
                .spacing(0, 0).generateLayout(bottomBar);
    }
    
    public void highlightUsing(ADocument document) {
        // TODO Auto-generated method stub
    }
    
    public void setText(String text) {
        sourceView.setText(text);
    }
    
    public void openWindow() {
        shell.open();
    }
    
    public OutlineView bindOutlineView(OutlineViewCallback callback) {
        return outlineView.bind(callback);
    }
    
    public SourceView bindSourceView(SourceViewCallback callback) {
        return sourceView.bind(callback);
    }
    
    public void fileClose() {
        if (callback.closeFile())
            shell.dispose();
    }

    public void askSaveDiscardCancel(SaveDiscardCancel handler) {
        Alert alert = new Alert(shell);
        alert.askSaveDiscardCancel(handler);
    }
    
    private static class Alert {
        
        private static final int sel_alertDidEnd_returnCode_contextInfo_ = OS.sel_registerName("alertDidEnd:returnCode:contextInfo:");
        private final Shell parent;
        private SaveDiscardCancel handler;
        
        private static void initClass() {
            Callback callback = new Callback(Alert.class, "delegateProc", 5);
            int proc = callback.getAddress();
            if (proc == 0)
                SWT.error(SWT.ERROR_NO_MORE_CALLBACKS);
            
            Display display = Display.getDefault();
            Callback windowDelegateCallback3 = new Callback(display, "windowDelegateProc", 3);
            int proc3 = windowDelegateCallback3.getAddress();
            if (proc3 == 0) SWT.error (SWT.ERROR_NO_MORE_CALLBACKS);
            Callback windowDelegateCallback2 = new Callback(display, "windowDelegateProc", 2);
            int proc2 = windowDelegateCallback2.getAddress();
            if (proc2 == 0) SWT.error (SWT.ERROR_NO_MORE_CALLBACKS);
            
            String className = "SWTAlertDelegate";
            int cls = OS.objc_allocateClassPair(OS.class_NSObject, className, 0);
            OS.class_addIvar(cls, "tag", OS.PTR_SIZEOF, (byte)(Math.log(OS.PTR_SIZEOF) / Math.log(2)), "i");
            OS.class_addMethod(cls, OS.sel_tag, proc2, "@:");
            OS.class_addMethod(cls, OS.sel_setTag_1, proc3, "@:i");
            OS.class_addMethod(cls, sel_alertDidEnd_returnCode_contextInfo_, proc, "@:@ii");
            OS.objc_registerClassPair(cls);
        }
        
        static {
            initClass();
        }
        
        public Alert(Shell parent) {
            if (parent == null)
                throw new NullPointerException("parent is null");
            this.parent = parent;
        }

        @SuppressWarnings("restriction")
        public void askSaveDiscardCancel(SaveDiscardCancel handler) {
            this.handler = handler;
            NSAlert alert = (NSAlert) new NSAlert().alloc().init();
            alert.setMessageText(NSString.stringWith("Save or discard?"));
            alert.setInformativeText(NSString.stringWith("This document has not been saved yet. You cannot close it without either saving or loosing all the entered data."));
            alert.addButtonWithTitle(NSString.stringWith("Save As..."));
            alert.addButtonWithTitle(NSString.stringWith("Cancel"));
            alert.addButtonWithTitle(NSString.stringWith("Discard"));
            
            SWTAlertDelegate delegate = (SWTAlertDelegate) new SWTAlertDelegate().alloc().init();
            int ref = OS.NewGlobalRef(this);
            if (ref == 0) SWT.error (SWT.ERROR_NO_HANDLES);
            delegate.setTag(ref);
            
            alert.beginSheetModalForWindow(parent.view.window(), delegate, 
                    sel_alertDidEnd_returnCode_contextInfo_, 0);
        }
        
        static int delegateProc(int id, int sel, int arg0, int arg1, int arg2) {
            SWTAlertDelegate delegate = new SWTAlertDelegate(id);
            int ref = delegate.tag();
            System.out.println("Ref = " + ref);
            Alert alert = (Alert) OS.JNIGetObject(ref);
//            OS.DeleteGlobalRef(ref);
            alert.finished(arg1);
            return 0;
        }

        private void finished(int button) {
            switch(button) {
            case OS.NSAlertFirstButtonReturn:
                handler.save();
                break;
            case OS.NSAlertSecondButtonReturn:
                handler.cancel();
                break;
            case OS.NSAlertThirdButtonReturn:
                handler.discard();
                break;
            }
        }
        
        
    }

    public void close() {
        shell.dispose();
    }
    
    //	public void consume(WorkspaceSnapshot snapshot) {
    //		title = APP_TITLE + " - " + CorchyApplication.workspace().storage().getDescription();
    //		Display display = Display.getDefault();
    //		if (display == null)
    //			return;
    //		display.asyncExec(new Runnable() {
    //			public void run() {
    //				Shell shell = getShell();
    //				if (shell != null)
    //					shell.setText(title);
    //			}
    //		});
    //
    //	}
    
}
