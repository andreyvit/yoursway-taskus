package com.mkalugin.corchy.ui.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class PlatformStuff {

	public static void setRepresentedFileName(Shell shell, String name) {
		shell.view.window().setRepresentedFilename(
				NSString.stringWith(name));
	}
	
	public static void setDocumentEdited(Shell shell, boolean edited) {
		shell.view.window().setDocumentEdited(edited);
	}
	
	public static void setApplicationMenuBar(Display display, Menu menu) {
		display.setApplicationMenuBar(menu);
	}

	public static void setPlaceholderString(Text text, String string) {
		text.setPlaceholderString(string);
	}
	
	public static Button texturedButton(Composite parent) {
		Button button = new Button(parent, SWT.NONE | SWT.PUSH);
        ((NSButton) button.view).setBezelStyle(OS.NSTexturedRoundedBezelStyle);
        ((NSButton) button.view).setImagePosition(OS.NSImageOnly);
        return button;
	}
	
}
