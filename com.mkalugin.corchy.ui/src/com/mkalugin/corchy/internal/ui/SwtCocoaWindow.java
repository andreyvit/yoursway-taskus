package com.mkalugin.corchy.internal.ui;

import java.io.File;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Shell;

import com.mkalugin.corchy.internal.ui.dialogs.GotItAlert;
import com.mkalugin.corchy.internal.ui.dialogs.PasswordSheet;
import com.mkalugin.corchy.internal.ui.dialogs.SynchronizationProgressDialog;
import com.mkalugin.corchy.internal.ui.editor.SwtCocoaSourceView;
import com.mkalugin.corchy.internal.ui.location.InitialShellPosition;
import com.mkalugin.corchy.internal.ui.location.WindowLocationConfiguration;
import com.mkalugin.corchy.internal.ui.location.WindowLocationManager;
import com.mkalugin.corchy.ui.controls.BasicAlert;
import com.mkalugin.corchy.ui.controls.BottomBarComposition;
import com.mkalugin.corchy.ui.controls.FileSheet;
import com.mkalugin.corchy.ui.controls.PlatformStuff;
import com.mkalugin.corchy.ui.core.DialogSettingsProvider;
import com.mkalugin.pikachu.core.controllers.search.SearchCallback;
import com.mkalugin.pikachu.core.controllers.search.SearchControls;
import com.mkalugin.pikachu.core.controllers.search.SearchResult;
import com.mkalugin.pikachu.core.controllers.viewglue.DocumentBinding;
import com.mkalugin.pikachu.core.controllers.viewglue.DocumentWindow;
import com.mkalugin.pikachu.core.controllers.viewglue.DocumentWindowCallback;
import com.mkalugin.pikachu.core.controllers.viewglue.FileNameRequestor;
import com.mkalugin.pikachu.core.controllers.viewglue.OutlineView;
import com.mkalugin.pikachu.core.controllers.viewglue.OutlineViewCallback;
import com.mkalugin.pikachu.core.controllers.viewglue.PasswordQueryAgent;
import com.mkalugin.pikachu.core.controllers.viewglue.PasswordResult;
import com.mkalugin.pikachu.core.controllers.viewglue.SaveDiscardCancel;
import com.mkalugin.pikachu.core.controllers.viewglue.SourceView;
import com.mkalugin.pikachu.core.controllers.viewglue.SourceViewCallback;
import com.mkalugin.pikachu.core.model.DocumentTypeDefinition;

public class SwtCocoaWindow implements DocumentWindow, SearchControls, PasswordQueryAgent {
    
    private static final String APP_TITLE = "Corchy";
    
    private SwtCocoaSourceView sourceView;
    private SwtCocoaOutlineView outlineView;
    private final String title = null;
    
    private final DocumentWindowCallback callback;
    
    private final Shell shell;
    
    private final DialogSettingsProvider preferenceStorageProvider;
    
    private final WindowLocationManager locationManager;
    
    private SearchComposition searchComposition;
    
    private SearchCallback searchCallback;
    
    private SynchronizationProgressDialog synchProgressSheet;
    
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
        shell.setMinimumSize(420, 200);
        
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
                initialPosition).size(400, 600));
    }
    
    public void setDocumentBinding(DocumentBinding documentBinding, boolean isDocumentEmpty) {
        locationManager.setDialogSettings(preferenceStorageProvider.forKey(documentBinding.getUniqueKey()));
        shell.setText(documentBinding.getFile().getName());
        PlatformStuff.setRepresentedFileName(shell, documentBinding.getFile().getPath());
        PlatformStuff.setDocumentEdited(shell, documentBinding.isUntitled() && !isDocumentEmpty);
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
        
        final Sash sash = new Sash(parent, SWT.VERTICAL);
        final Color sashColor = new Color(sash.getDisplay(), 139, 139, 139);
        sash.addPaintListener(new PaintListener() {
            
            public void paintControl(PaintEvent e) {
                Rectangle sashRect = sash.getBounds();
                e.gc.setForeground(sashColor);
                e.gc.drawLine(0, 0, 0, sashRect.height);
            }
            
        });
        sash.setEnabled(false); //! don't work anyway
        
        sourceView = new SwtCocoaSourceView(parent);
        
        ///
        
        outlineView.setLayoutData(GridDataFactory.fillDefaults().grab(false, true).minSize(150, SWT.DEFAULT)
                .hint(150, SWT.DEFAULT).create());
        
        sash.setLayoutData(GridDataFactory.fillDefaults().grab(false, true).minSize(1, SWT.DEFAULT).hint(1,
                SWT.DEFAULT).create());
        
        sourceView.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL)
                .create());
        
        GridLayoutFactory.fillDefaults().numColumns(3).spacing(0, 0).generateLayout(parent);
    }
    
    private void createControls(Composite parent) {
        Composite editorBlock = new Composite(parent, SWT.NULL);
        createEditorAndOutline(editorBlock);
        editorBlock.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true)
                .create());
        GridLayoutFactory.fillDefaults().extendedMargins(0, 0, 0, 0).spacing(0, 0).numColumns(1)
                .generateLayout(parent);
    }
    
    private void fillBottomBar(Composite bottomBar) {
        //		Button syncButton = texturedButton(bottomBar);
        //		syncButton.setImage(IMG_SYNC.get());
        //		syncButton.setLayoutData(GridDataFactory.defaultsFor(syncButton).align(SWT.BEGINNING,
        //				SWT.BEGINNING).indent(0, 0).hint(32, SWT.DEFAULT).create());
        //		syncButton.addSelectionListener(new SelectionAdapter() {
        //			@Override
        //			public void widgetSelected(SelectionEvent e) {
        //				callback.startSynchronization();
        //			}
        //		});
        
        searchComposition = new SearchComposition(bottomBar);
        searchComposition.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true,
                true).indent(0, -5).create());
        
        Composite endSpace = new Composite(bottomBar, SWT.TRANSPARENT);
        endSpace.setLayoutData(GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).indent(0, 0).hint(15,
                15).create());
        
        GridLayoutFactory.fillDefaults().numColumns(3).extendedMargins(8, 8, 4, 0).margins(0, 0)
                .spacing(0, 0).generateLayout(bottomBar);
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
    
    public void askSaveDiscardCancel(final SaveDiscardCancel handler) {
        BasicAlert alert = new BasicAlert(shell) {
            
            {
                setMessageText("Save or discard?");
                setInformativeText("This document has not been saved yet. You cannot close it without either saving it or loosing all the entered data.");
                addButton("Save As...");
                addButton("Cancel");
                addButton("Discard");
            }
            
            @Override
            protected void finished(int button) {
                switch (button) {
                case 0:
                    dismiss();
                    handler.save();
                    break;
                case 1:
                    dismiss();
                    handler.cancel();
                    shell.setActive();
                    break;
                case 2:
                    handler.discard();
                    break;
                }
            }
            
        };
        alert.open();
    }
    
    public void showAlertWithMessage(final String title, final String message) {
        final BasicAlert alert = new BasicAlert(shell) {
            
            {
                setMessageText(title);
                setInformativeText(message);
                addButton("OK");
            }
            
            @Override
            protected void finished(int button) {
                shell.setActive();
            }
            
        };
        Display.getDefault().syncExec(new Runnable() {
            
            public void run() {
                alert.open();
            }
            
        });
    }
    
    public void close() {
        shell.dispose();
    }
    
    public void fileSaveAs() {
        callback.saveFileAs();
    }
    
    public void chooseFileNameToSaveInto(DocumentBinding binding,
            DocumentTypeDefinition documentTypeDefinition, final FileNameRequestor requestor) {
        FileSheet dialog = new FileSheet(shell, SWT.SAVE) {
            
            @Override
            protected void finished(String result) {
                if (result == null)
                    requestor.cancelled();
                else
                    requestor.fileSelected(new File(result));
            }
            
        };
        dialog
                .setFilterExtensions(new String[] { "*." + documentTypeDefinition.defaultExtension(), "*.txt" });
        dialog.setFilterNames(new String[] { "Corchy documents", "Text files" });
        dialog.setText("Save TODO list as");
        dialog.open();
    }
    
    public void reportSavingFailed(File file) {
        BasicAlert alert = new GotItAlert(null);
        alert.setMessageText("Saving failed");
        alert.setInformativeText(String.format("Could not write into file “%s”.", file.getPath()));
        alert.openModal();
    }
    
    public SearchControls bindSearchControls(SearchCallback callback) {
        searchCallback = callback;
        if (searchComposition != null)
            searchComposition.setCallback(callback);
        return this;
    }
    
    public void hightlightMatch(int number) {
        sourceView.highlightMatch(number);
    }
    
    public void setMatchesNavigationEnabled(boolean enabled) {
        searchComposition.setNavigationVisible(enabled);
    }
    
    public void showSearchResult(final SearchResult result) {
        Display.getCurrent().syncExec(new Runnable() {
            
            public void run() {
                searchComposition.setMatchesCount(result.matchesCount());
            }
            
        });
        sourceView.highlightSearchResults(result);
    }
    
    public void clearSearchField() {
        if (searchComposition != null)
            searchComposition.setText("");
    }
    
    public void setEditorSelection(int start, int end) {
        sourceView.setSelection(start, end);
    }
    
    public void switchFocusToEditor() {
        sourceView.setFocus();
    }
    
    public void switchFocusToSearch() {
        searchComposition.setFocus();
    }
    
    public void findNext() {
        if (searchComposition.navigationEnabled())
            searchCallback.nextMatch();
    }
    
    public void findPrevious() {
        if (searchComposition.navigationEnabled())
            searchCallback.previousMatch();
    }
    
    public void closeSynchProgressSheet() {
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                synchProgressSheet.dismiss();
            };
        });
    }
    
    public void openSynchProgressSheet() {
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                synchProgressSheet = new SynchronizationProgressDialog(shell);
                synchProgressSheet.open();
            }
        });
    }
    
    public void setSynchProgressMessage(final String text) {
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                synchProgressSheet.setText(text);
            }
        });
    }
    
    public void fileSynchronizeNow() {
        callback.startSynchronization(null);
    }
    
    public PasswordQueryAgent bindPasswordQueryAgent() {
        return this;
    }
    
    public PasswordResult askPassword(final String domain, final String login, final boolean canStore) {
        closeSynchProgressSheet();
        try {
            final PasswordResult[] result = { null };
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    PasswordSheet sheet = new PasswordSheet(shell, domain, login, canStore);
                    result[0] = sheet.runModal();
                }
            });
            return result[0];
        } finally {
            openSynchProgressSheet();
        }
    }
    
    public void runAsync(Runnable runnable) {
        Display.getDefault().asyncExec(runnable);
    }
    
    public void runSync(Runnable runnable) {
        Display.getDefault().syncExec(runnable);
    }
    
}
