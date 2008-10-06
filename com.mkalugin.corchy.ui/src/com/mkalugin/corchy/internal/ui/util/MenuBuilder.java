package com.mkalugin.corchy.internal.ui.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class MenuBuilder {
    
    private final Menu menu;
    
    public MenuBuilder(Menu menu) {
        this.menu = menu;
    }
    
    public MenuItem cascade(String title, int accel, Menu submenu) {
        MenuItem item = createItem(title, accel, SWT.CASCADE);
        item.setMenu(submenu);
        return item;
    }
    
    public MenuItem item(String title, int accel, final Runnable action) {
        MenuItem item = createItem(title, accel, SWT.PUSH);
        item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                action.run();
            }
        });
        return item;
    }
    
    public MenuItem item(String title, final Runnable action) {
        return item(title, -1, action);
    }
    
    public void separator() {
        new MenuItem(menu, SWT.SEPARATOR);
    }
    
    private MenuItem createItem(String title, int accel, int style) {
        MenuItem item = new MenuItem(menu, style);
        if (accel >= 0)
            item.setAccelerator(accel);
        item.setText(title + windowsShortcutHint(accel));
        return item;
    }
    
    private static String windowsShortcutHint(int accel) {
        if (accel < 0)
            return "";
        
        StringBuilder sb = new StringBuilder("\t");
        
        if ((accel & SWT.MOD1 | SWT.CTRL) != 0)
            sb.append("Ctrl+");
        if ((accel & SWT.ALT) != 0)
            sb.append("Alt+");
        if ((accel & SWT.SHIFT) != 0)
            sb.append("Shift+");
        
        switch ((char) accel) {
        case ' ':
            sb.append("Space");
            break;
        case SWT.TAB:
            sb.append("Tab");
            break;
        case SWT.CR:
        case SWT.LF:
            sb.append("Enter");
            break;
        case SWT.BS:
            sb.append("Backspace");
            break;
        default:
            sb.append((char) accel);
        }
        
        return sb.toString();
    }
}
