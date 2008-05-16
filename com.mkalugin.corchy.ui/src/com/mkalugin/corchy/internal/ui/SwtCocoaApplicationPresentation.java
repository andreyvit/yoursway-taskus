package com.mkalugin.corchy.internal.ui;

import static com.google.common.collect.Lists.newArrayList;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.mkalugin.corchy.internal.editor.CorchyViewer;
import com.mkalugin.corchy.internal.ui.location.InitialShellPosition;
import com.mkalugin.corchy.ui.core.preference.IPreferenceStore;
import com.mkalugin.corchy.ui.core.preference.SubPreferenceStore;
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
        
        builder.separator();
        
        builder.cascade("Find", -1, createFindMenu(shell));
        
        return menu;
    }
    
    private Menu createFindMenu(Shell shell) {
    	 Menu menu = new Menu(shell, SWT.DROP_DOWN);
         
         MenuBuilder builder = new MenuBuilder(menu);
         
         builder.item("Document Search", SWT.MOD1 + SWT.MOD3 + 'F', new Runnable() {
             public void run() {
                 activeWindow.switchFocusToSearch();
             }
         });
         builder.separator();
         builder.item("Find Next", SWT.MOD1 + 'G', new Runnable() {
             public void run() {
            	 activeWindow.findNext();
             }
         });
         builder.item("Find Previous", SWT.MOD1 + SWT.SHIFT + 'G', new Runnable() {
             public void run() {
            	 activeWindow.findPrevious();
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
        builder.separator();
        fileClose = builder.item("Close", SWT.MOD1 + 'W', new Runnable() {
            public void run() {
                if (activeWindow != null)
                    activeWindow.fileClose();
            }
        });
        builder.item("Save As...", SWT.MOD1 + SWT.SHIFT + 'S', new Runnable() {
            public void run() {
                if (activeWindow != null)
                    activeWindow.fileSaveAs();
            }
        });
        
        builder.separator();
        
        builder.item("Synchronize Now", new Runnable() {
            public void run() {
                //                performSync();
            }
        });
        
        return menu;
    }
    
    
    Menu createEntryMenu(final Shell shell) {
        Menu menu = new Menu(shell, SWT.DROP_DOWN);
        
        MenuBuilder builder = new MenuBuilder(menu);
        builder.item("Tag with Done", SWT.MOD1 + 'D', new Runnable() {
            public void run() {
                Control focusControl = Display.getCurrent().getFocusControl();
                if (focusControl instanceof StyledText) {
                    CorchyViewer viewer = CorchyViewer.fromControl((StyledText) focusControl);
                    if (viewer != null) {
                        StyledText text = viewer.getTextWidget();
                        IDocument document = viewer.getDocument();
                        int offset = text.getCaretOffset();
                        try {
                            int line = document.getLineOfOffset(offset);
                            int lineOffset = document.getLineOffset(line);
                            int lineLength = document.getLineLength(line);
                            String lineText = document.get(lineOffset, lineLength);
                            Matcher matcher = Pattern.compile(" @done\\b").matcher(lineText);
                            if (matcher.find()) {
                                text.replaceTextRange(lineOffset + matcher.start(), matcher.end() - matcher.start(), "");
                            } else {
                                String delimiter = document.getLineDelimiter(line);
                                int lineBodyEnd = lineOffset + lineLength
                                        - (delimiter == null ? 0 : delimiter.length());
                                text.replaceTextRange(lineBodyEnd, 0, " @done");
                            }
                        } catch (BadLocationException e) {
                            e.printStackTrace();
                        }
                    }
                }
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
        
        MenuItem entryItem = new MenuItem(bar, SWT.CASCADE);
        entryItem.setText("Entry");
        entryItem.setMenu(createEntryMenu(shell));
        
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
        activeWindow = window;
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
