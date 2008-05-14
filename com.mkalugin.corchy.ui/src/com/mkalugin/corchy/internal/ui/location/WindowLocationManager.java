package com.mkalugin.corchy.internal.ui.location;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;


public class WindowLocationManager {
    
    private static final String KEY_WIDTH = "width";
    private static final String KEY_HEIGHT = "height";
    private static final String KEY_Y = "y";
    private static final String KEY_X = "x";
    
    private IDialogSettings dialogSettings;
    private final Shell shell;
    
    private InitialShellPosition initialPosition;
    private Point defaultSize;
    private ShellPositionConstraint positionConstraint;
    private boolean persistLocation;
    private boolean persistSize;
    
    public WindowLocationManager(Shell shell, IDialogSettings dialogSettings,
            WindowLocationConfiguration configuration) {
        if (shell == null)
            throw new NullPointerException("shell is null");
        if (configuration == null)
            throw new NullPointerException("configuration is null");
        this.shell = shell;
        this.initialPosition = configuration.getInitialPosition();
        this.defaultSize = configuration.getDefaultSize();
        this.positionConstraint = configuration.getPositionConstraint();
        this.persistLocation = configuration.shouldPersistLocation();
        this.persistSize = configuration.shouldPersistSize();
        
        if (dialogSettings == null && configuration.shouldPersistSomething())
            throw new NullPointerException("dialogSettings is null, but persistance is requestored");
        this.dialogSettings = dialogSettings;
        
        Listener saveStateListener = new Listener() {
            public void handleEvent(Event event) {
                saveState();
            }
        };
        shell.addListener(SWT.Resize, saveStateListener);
        shell.addListener(SWT.Move, saveStateListener);
        initializeBounds(shell);
    }
    
    public void setDialogSettings(IDialogSettings dialogSettings) {
        if (dialogSettings == null && persistLocation || persistSize)
            throw new NullPointerException("dialogSettings is null, but persistance is requestored");
        this.dialogSettings = dialogSettings;
    }
    
    private void initializeBounds(Shell shell) {
        Point size = loadSize();
        if (size == null)
            size = computeInitialSize(shell);
        Point location = loadLocation();
        location = initialPosition.calculatePosition(shell.getDisplay(), (Shell) shell.getParent(), size);
        if (location == null)
            location = shell.getLocation();
        shell.setBounds(positionConstraint.constrain(new Rectangle(location.x, location.y, size.x, size.y),
                shell.getDisplay()));
    }
    
    private Point computeInitialSize(Shell shell) {
        Point size = shell.computeSize(defaultSize.x, defaultSize.y, true);
        if (defaultSize.x != SWT.DEFAULT)
            size.x = defaultSize.x;
        if (defaultSize.y != SWT.DEFAULT)
            size.y = defaultSize.y;
        return size;
    }
    
    private Point loadSize() {
        if (!persistSize)
            return null;
        try {
            return new Point(dialogSettings.getInt(KEY_WIDTH), dialogSettings.getInt(KEY_HEIGHT));
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    private Point loadLocation() {
        if (!persistLocation)
            return null;
        try {
            return new Point(dialogSettings.getInt(KEY_X), dialogSettings.getInt(KEY_Y));
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    private void saveState() {
        Rectangle bounds = shell.getBounds();
        if (persistLocation) {
            dialogSettings.put(KEY_X, bounds.x);
            dialogSettings.put(KEY_Y, bounds.y);
        }
        if (persistSize) {
            dialogSettings.put(KEY_WIDTH, bounds.width);
            dialogSettings.put(KEY_HEIGHT, bounds.height);
        }
    }
    
}
