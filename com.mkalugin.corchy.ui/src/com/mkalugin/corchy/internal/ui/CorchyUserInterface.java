package com.mkalugin.corchy.internal.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.mkalugin.corchy.internal.editor.CorchyViewer;
import com.mkalugin.pikachu.core.ICorchyWindow;

public class CorchyUserInterface {
    
    private Display display;
    private Shell hiddenShell;
    
    public CorchyUserInterface() {
        display = new Display();
        Display.setAppName("Corchy");
        
        hiddenShell = new Shell();
        display.setApplicationMenuBar(createMenuBar(hiddenShell));
//        display.setApplicationMenuName("Corchy");
    }
    
    public ICorchyWindow createDocumentWindow() {
        return new CorchyWindow(display);
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
        builder.item("Open...", SWT.MOD1 + 'O', new Runnable() {
            public void run() {
//                DirectoryDialog fileDialog = new DirectoryDialog(getShell(), SWT.OPEN);
//                while (true) {
//                    String result = fileDialog.open();
//                    if (result == null)
//                        break;
                    //                  try {
                    //                      FSDataStorage dataStorage = new FSDataStorage(new File(result), false);
                    //                      CorchyApplication.openWorkspaceWithStorage(dataStorage);
                    //                      break;
                    //                  } catch (Exception e) {
                    //                      MessageBox messageBox = new MessageBox(getShell(), SWT.OK | SWT.CANCEL);
                    //                      messageBox.setMessage("Impossible to open");
                    //                      messageBox.setText("Failed to open file " + result + " due to:\n"
                    //                              + e.getLocalizedMessage()
                    //                              + "\nPlease select other file or click 'cancel'.");
                    //                      int open = messageBox.open();
                    //                      if (open == SWT.CANCEL)
                    //                          break;
                    //                  }
//                }
            }
        });
        builder.item("Save as...", SWT.MOD1 + SWT.SHIFT + 'S', new Runnable() {
            public void run() {
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
    
}
