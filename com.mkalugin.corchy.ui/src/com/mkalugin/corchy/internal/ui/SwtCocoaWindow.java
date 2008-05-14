package com.mkalugin.corchy.internal.ui;

import static com.mkalugin.corchy.internal.images.CorchyImages.ICN_SYNC;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.cocoa.NSButton;
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
import org.eclipse.swt.widgets.Text;

import com.mkalugin.corchy.internal.editor.SwtCocoaSourceView;
import com.mkalugin.pikachu.core.ast.ADocument;
import com.mkalugin.pikachu.core.controllers.viewglue.DocumentWindow;
import com.mkalugin.pikachu.core.controllers.viewglue.DocumentWindowCallback;
import com.mkalugin.pikachu.core.controllers.viewglue.OutlineView;
import com.mkalugin.pikachu.core.controllers.viewglue.OutlineViewCallback;
import com.mkalugin.pikachu.core.controllers.viewglue.SourceView;
import com.mkalugin.pikachu.core.controllers.viewglue.SourceViewCallback;

public class SwtCocoaWindow extends MainWindow implements DocumentWindow {
    
    private static final String DIALOG_ID = "mainWindow";
    
    private static final String APP_TITLE = "Corchy";
    
    private static final String EXT = ".corchy";
    
    private SwtCocoaSourceView sourceView;
    private SwtCocoaOutlineView outlineView;
    private String title = null;
    
    private DocumentWindowCallback callback;
    
    public SwtCocoaWindow(Display display, DocumentWindowCallback callback) {
        super(display, true);
        
        if (callback == null)
            throw new NullPointerException("callback is null");
        this.callback = callback;
        create();
    }
    
    @Override
    protected void dispose() {
        sourceView.dispose();
    }
    
    @Override
    protected String title() {
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
    
    @Override
    protected void createControls(Composite parent) {
        Composite editorBlock = new Composite(parent, SWT.NULL);
        createEditorAndOutline(editorBlock);
        editorBlock.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true)
                .create());
    }
    
    @Override
    protected void fillBottomBar(Composite bottomBar) {
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
                performSync();
            }
        });
        
        // Search field
        Text searchField = new Text(bottomBar, SWT.SINGLE | SWT.SEARCH);
        searchField.setText("Search");
        searchField.setLayoutData(GridDataFactory.defaultsFor(searchField).align(SWT.END, SWT.BEGINNING)
                .indent(0, 0).create());
    }
    
    protected void performSync() {
        //		try {
        //			CorchyApplication.workspace().synchronize();
        //		} catch (StorageException e1) {
        //			// TODO
        //		}
    }
    
    @Override
    protected String dialogId() {
        return DIALOG_ID;
    }
    
    public void highlightUsing(ADocument document) {
        // TODO Auto-generated method stub
        
    }
    
    public void setText(String text) {
        sourceView.setText(text);
    }
    
    public void openWindow() {
        open();
    }
    
    public OutlineView bindOutlineView(OutlineViewCallback callback) {
        return outlineView.bind(callback);
    }
    
    public SourceView bindSourceView(SourceViewCallback callback) {
        return sourceView.bind(callback);
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
