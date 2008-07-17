package com.mkalugin.corchy.ui.controls;

import java.lang.reflect.Field;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class PlatformStuff {

	public static void setRepresentedFileName(Shell shell, String name) {
//		shell.view.window().setRepresentedFilename(
//				NSString.stringWith(name));
	}
	
	public static void setDocumentEdited(Shell shell, boolean edited) {
//		shell.view.window().setDocumentEdited(edited);
	}
	
	private static void setMenuParent(Menu menu, Object parent) {
		Field field;
		try {
			field = Menu.class.getDeclaredField("parent");
			field.setAccessible(true);
			field.set(menu, parent);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void setApplicationMenuBar(Display display, final Menu menu) {
//		display.setApplicationMenuBar(menu);	
		display.addFilter(SWT.Dispose, new Listener() {

			public void handleEvent(Event event) {
				if (event.widget instanceof Shell) {
					Shell shell = (Shell) event.widget;
					Menu menuBar = shell.getMenuBar();
					if (menuBar == menu) {
						setMenuParent(menu, null);
						shell.setMenuBar(null);
					}
				}
			}
			
		});
		display.addFilter(SWT.Activate, new Listener() {

			private Shell lastActiveShell = null;
			
			public void handleEvent(Event event) {
				Shell activeShell = event.display.getActiveShell();
				if (activeShell != null) {
					if (lastActiveShell != null && !lastActiveShell.isDisposed())
						lastActiveShell.setMenuBar(null);
						setMenuParent(menu, activeShell);
						activeShell.setMenuBar(menu);
						lastActiveShell = activeShell;

				}
			}
			
		});
	}

	public static void setPlaceholderString(Text text, String string) {
//		text.setPlaceholderString(string);
	}
	
	public static Button texturedButton(Composite parent) {
		Button button = new Button(parent, SWT.FLAT);
        return button;
	}
	
}
