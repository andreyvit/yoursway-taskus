package com.mkalugin.corchy.internal.ui;

import static com.google.common.collect.Lists.newArrayList;
import static com.mkalugin.corchy.internal.ui.Utils.lookup;

import java.io.File;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.internal.cocoa.NSAlert;
import org.eclipse.swt.internal.cocoa.NSString;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.mkalugin.corchy.internal.editor.CorchyViewer;
import com.mkalugin.corchy.internal.ui.location.InitialShellPosition;
import com.mkalugin.pikachu.core.controllers.viewglue.ApplicationPresentation;
import com.mkalugin.pikachu.core.controllers.viewglue.ApplicationPresentationCallback;
import com.mkalugin.pikachu.core.controllers.viewglue.DocumentWindow;
import com.mkalugin.pikachu.core.controllers.viewglue.DocumentWindowCallback;
import com.mkalugin.pikachu.core.model.DocumentTypeDefinition;

public class SwtCocoaApplicationPresentation implements ApplicationPresentation {
    
    private Display display;
    private Shell hiddenShell;
    private final ApplicationPresentationCallback callback;
    private final IPreferenceStore preferenceStore;
    
    private final List<SwtCocoaWindow> documentWindows = newArrayList();
    private MenuItem fileClose;
    SwtCocoaWindow activeWindow;
    
    public SwtCocoaApplicationPresentation(ApplicationPresentationCallback callback,
            IPreferenceStore preferenceStore) {
        if (callback == null)
            throw new NullPointerException("callback is null");
        if (preferenceStore == null)
            throw new NullPointerException("preferenceStore is null");
        this.callback = callback;
        this.preferenceStore = preferenceStore;
        
        Display.setAppName("Corchy");
        display = new Display();
        
        hiddenShell = new Shell();
        display.setApplicationMenuBar(createMenuBar(hiddenShell));
        //        display.setApplicationMenuName("Corchy");
    }
    
    public void run() {
        while (!display.isDisposed())
            if (!display.readAndDispatch())
                display.sleep();
    }
    
    Menu createEditMenu(Shell shell) {
        Menu menu = new Menu(shell, SWT.DROP_DOWN);
        
        MenuBuilder builder = new MenuBuilder(menu);
        
        builder.item("Undo", SWT.MOD1 + 'Z', new Runnable() {
            public void run() {
                Control focusControl = Display.getCurrent().getFocusControl();
                if (focusControl instanceof StyledText) {
                    CorchyViewer viewer = CorchyViewer.fromControl((StyledText) focusControl);
                    if (viewer != null)
                        viewer.getUndoManager().undo();
                }
            }
        });
        builder.item("Redo", SWT.MOD1 + SWT.SHIFT + 'Y', new Runnable() {
            public void run() {
                Control focusControl = Display.getCurrent().getFocusControl();
                if (focusControl instanceof StyledText) {
                    CorchyViewer viewer = CorchyViewer.fromControl((StyledText) focusControl);
                    if (viewer != null)
                        viewer.getUndoManager().redo();
                }
            }
        });
        builder.separator();
        builder.item("Cut", SWT.MOD1 + 'X', new Runnable() {
            public void run() {
                Control focusControl = Display.getCurrent().getFocusControl();
                if (focusControl instanceof StyledText) {
                    ((StyledText) focusControl).cut();
                } else if (focusControl instanceof Text) {
                    ((Text) focusControl).cut();
                }
            }
        });
        builder.item("Copy", SWT.MOD1 + 'C', new Runnable() {
            public void run() {
                Control focusControl = Display.getCurrent().getFocusControl();
                if (focusControl instanceof StyledText) {
                    ((StyledText) focusControl).copy();
                } else if (focusControl instanceof Text) {
                    ((Text) focusControl).copy();
                }
            }
        });
        builder.item("Paste", SWT.MOD1 + 'V', new Runnable() {
            public void run() {
                Control focusControl = Display.getCurrent().getFocusControl();
                if (focusControl instanceof StyledText) {
                    ((StyledText) focusControl).paste();
                } else if (focusControl instanceof Text) {
                    ((Text) focusControl).paste();
                }
            }
        });
        builder.item("Select All", SWT.MOD1 + 'A', new Runnable() {
            public void run() {
                Control focusControl = Display.getCurrent().getFocusControl();
                if (focusControl instanceof StyledText) {
                    ((StyledText) focusControl).selectAll();
                }
                if (focusControl instanceof Text) {
                    ((Text) focusControl).selectAll();
                }
            }
        });
        
        return menu;
    }
    
    Menu createFileMenu(final Shell shell) {
        Menu menu = new Menu(shell, SWT.DROP_DOWN);
        
        MenuBuilder builder = new MenuBuilder(menu);
        builder.item("New", SWT.MOD1 + 'N', new Runnable() {
            public void run() {
                callback.openNewDocument();
            }
        });
        builder.item("Open...", SWT.MOD1 + 'O', new Runnable() {
            public void run() {
                callback.openDocument();
            }
        });
        builder.item("Save As...", SWT.MOD1 + SWT.SHIFT + 'S', new Runnable() {
            public void run() {
                if (activeWindow != null)
                    activeWindow.fileSaveAs();
                //                FileDialog fileDialog = new FileDialog(getShell(), SWT.SAVE);
                //                fileDialog.setFilterExtensions(new String[] { EXT });
                //                fileDialog.setFileName("Unnamed." + EXT);
                //                while (true) {
                //                    String result = fileDialog.open();
                //                    if (result == null)
                //                        break;
                //                    if (!result.endsWith(EXT))
                //                        result += EXT;
                //                  try {
                //                      FSDataStorage dataStorage = new FSDataStorage(new File(result), true);
                //                      CorchyApplication.workspace().saveToStorage(dataStorage);
                //                      CorchyApplication.openWorkspaceWithStorage(dataStorage);
                //                      break;
                //                  } catch (Exception e) {
                //                      MessageBox messageBox = new MessageBox(getShell(), SWT.OK | SWT.CANCEL);
                //                      messageBox.setMessage("Impossible to save");
                //                      messageBox.setText("Failed to save to file " + result + " due to:\n"
                //                              + e.getLocalizedMessage()
                //                              + "\nPlease select other file or click 'cancel'.");
                //                      int open = messageBox.open();
                //                      if (open == SWT.CANCEL)
                //                          break;
                //                  }
                //                }
            }
        });
        fileClose = builder.item("Close", SWT.MOD1 + 'W', new Runnable() {
            public void run() {
                if (activeWindow != null)
                    activeWindow.fileClose();
            }
        });
        
        builder.separator();
        
        builder.item("Synchronize with remotes", new Runnable() {
            public void run() {
                //                performSync();
            }
        });
        
        return menu;
    }
    
    Menu createMenuBar(Shell shell) {
        Menu bar = new Menu(shell, SWT.BAR);
        
        MenuItem fileItem = new MenuItem(bar, SWT.CASCADE);
        fileItem.setText("File");
        fileItem.setMenu(createFileMenu(shell));
        
        MenuItem editItem = new MenuItem(bar, SWT.CASCADE);
        editItem.setText("Edit");
        editItem.setMenu(createEditMenu(shell));
        return bar;
    }
    
    void setActiveWindow(SwtCocoaWindow window) {
        this.activeWindow = window;
        fileClose.setEnabled(activeWindow != null);
    }
    
    public DocumentWindow createDocumentWindow(DocumentWindowCallback callback) {
        DialogSettingsProvider dsp = new DialogSettingsProvider(new SubPreferenceStore(preferenceStore, "documentWindows"));
        InitialShellPosition pos = (documentWindows.isEmpty() ? InitialShellPosition.CENTERED
                : InitialShellPosition.SYSTEM_DEFAULT);
        final SwtCocoaWindow window = new SwtCocoaWindow(display, dsp, callback, pos);
        documentWindows.add(window);
        window.getShell().addDisposeListener(new DisposeListener() {
            
            public void widgetDisposed(DisposeEvent e) {
                documentWindows.remove(window);
                if (activeWindow == window)
                    setActiveWindow(null);
            }
            
        });
        window.getShell().addShellListener(new ShellAdapter() {
            
            @Override
            public void shellActivated(ShellEvent e) {
                setActiveWindow(window);
            }
            
            @Override
            public void shellDeactivated(ShellEvent e) {
                if (activeWindow == window)
                    setActiveWindow(null);
            }
            
        });
        return window;
    }
    
    public File chooseDocumentToOpen(DocumentTypeDefinition documentTypeDefinition) {
        Shell fakeShell = new Shell();
        try {
            FileDialog dialog = new FileDialog(fakeShell, SWT.OPEN);
            dialog.setFilterExtensions(new String[] { "*." + documentTypeDefinition.defaultExtension(),
                    "*.txt" });
            dialog.setFilterNames(new String[] { "Corchy documents", "Text files" });
            dialog.setText("Open TODO list");
            String choice = dialog.open();
            if (choice == null)
                return null;
            else
                return new File(choice);
        } finally {
            fakeShell.dispose();
        }
    }
    
    public void displayFailedToOpenError(File file) {
        CocoaAlert alert = new SimpleCocoaAlert(null);
        alert.setMessageText("Opening failed");
        alert.setInformativeText(String.format("Could not read from file “%s”.", file.getPath()));
        alert.openModal();
    }

    public void displayFailedToCreateEmptyDocumentError() {
        CocoaAlert alert = new SimpleCocoaAlert(null);
        alert.setMessageText("Failed to create a document");
        alert.setInformativeText("You've just triggered a disk I/O error #-4982063, you bastard!");
        alert.openModal();
    }
    
}
