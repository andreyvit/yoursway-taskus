package com.mkalugin.corchy.internal.ui.dialogs;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.internal.cocoa.NSText;
import org.eclipse.swt.internal.cocoa.NSTextField;
import org.eclipse.swt.internal.cocoa.NSTextFieldCell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.mkalugin.corchy.internal.ui.images.CorchyImages;
import com.mkalugin.pikachu.core.controllers.viewglue.PasswordResult;

public class PasswordSheet extends SheetDialog {
    
    private final String domain;
    private final String login;
    private PasswordResult result;

    public PasswordSheet(Shell parent, String domain, String login) {
        super(parent);
        if (domain == null)
            throw new NullPointerException("domain is null");
        if (login == null)
            throw new NullPointerException("login is null");
        this.domain = domain;
        this.login = login;
    }

    @Override
    protected Shell createShell() {
        final Shell dialog = new Shell((Shell) null);
        dialog.setText("Login");
        
        Label icon = new Label(dialog, SWT.NONE);
        icon.setImage(CorchyImages.IMG_KEYCHAIN.get());
        icon.setLayoutData(GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.TOP).create());
        
        Composite content = new Composite(dialog, SWT.NONE);
        
        Label label = new Label(content, SWT.WRAP);
        label.setText(String.format("Please login to %s.", domain));
        label.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false)
                .create());
        
        Composite inputComposite = new Composite(content, SWT.NONE);
        inputComposite.setLayoutData(GridDataFactory.fillDefaults().indent(0, 12).grab(true, true).create());
        
        Font regularFont = new Font(dialog.getDisplay(), "Arial", 16, SWT.NONE);
        
        Label nameLabel = new Label(inputComposite, SWT.NONE);
        nameLabel.setText("Name:");
        nameLabel.setFont(regularFont);
        
        Label nameValue = new Label(inputComposite, SWT.NONE);
        nameValue.setText(login);
        nameValue.setFont(regularFont);
        
        Label passwordLabel = new Label(inputComposite, SWT.NONE);
        passwordLabel.setText("Password:");
        
        final Text passwordValue = new Text(inputComposite, SWT.BORDER | SWT.PASSWORD);
        passwordValue.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        
        GridLayoutFactory.fillDefaults().numColumns(2).spacing(8, 8).generateLayout(inputComposite);
        
        final Button storeInKeyChainCheckbox = new Button(content, SWT.CHECK);
        storeInKeyChainCheckbox.setText("Remember this password in my keychain");
        storeInKeyChainCheckbox.setLayoutData(GridDataFactory.fillDefaults().indent(0, 12).create());
        
        Composite buttons = new Composite(content, SWT.NONE);
        buttons.setLayoutData(GridDataFactory.fillDefaults().indent(0, 4).grab(true, false).align(SWT.TRAIL,
                SWT.CENTER).create());
        
        Button cancelButton = new Button(buttons, SWT.NONE);
        cancelButton.setText("    Cancel    ");
        cancelButton.setLayoutData(GridDataFactory.fillDefaults().minSize(100, 0).create());
        
        Button loginButton = new Button(buttons, SWT.NONE);
        loginButton.setText("    Login    ");
        dialog.setDefaultButton(loginButton);
        loginButton.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).hint(150, 0)
                .create());
        
        GridLayoutFactory.fillDefaults().numColumns(2).spacing(2, 8).generateLayout(buttons);
        
        GridLayoutFactory.fillDefaults().spacing(8, 8).generateLayout(content);
        
        GridLayoutFactory.fillDefaults().margins(24, 20).spacing(8, 8).numColumns(2).generateLayout(dialog);
        
        dialog.setSize(dialog.computeSize(420, SWT.DEFAULT));
        
        cancelButton.addSelectionListener(new SelectionAdapter() {
            
            public void widgetSelected(SelectionEvent e) {
                dismiss();
            }
            
        });
        loginButton.addSelectionListener(new SelectionAdapter() {
            
            public void widgetSelected(SelectionEvent e) {
                result = new PasswordResult(passwordValue.getText(), storeInKeyChainCheckbox.getSelection());
                dismiss();
            }
            
        });
        return dialog;
    }
    
    public PasswordResult runModal() {
        open();
        Display display = dialog.getDisplay();
        while (!dialog.isDisposed())
            if (!display.readAndDispatch())
                display.sleep();
        return result;
    }
    
}
