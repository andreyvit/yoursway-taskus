package com.mkalugin.corchy.internal.ui;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.cocoa.NSButton;
import org.eclipse.swt.internal.cocoa.OS;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class CorchyWindow extends Window {

	private static String APP_NAME = "Corchy";

	private CorchyDrawer drawer;
	
	private CorchyEditor editor;
	
	private Point previousLocation = null;
	private Point previousSize = null;
	
	public CorchyWindow() {
		super((Shell)null);
	}

	@Override
	protected void configureShell(Shell shell) {
		shell.setText(APP_NAME);	
		shell.addListener(SWT.Dispose, new Listener(){
			public void handleEvent(Event event) {
				drawer.dispose();
				editor.dispose();
				saveState();
			}
		});		
		GridLayoutFactory.fillDefaults().extendedMargins(0, 0, 0, 0).generateLayout(shell);
	}
		
	@Override
	protected Control createContents(Composite parent) {
		drawer = new CorchyDrawer(getShell());
		drawer.open();
		
		editor = new CorchyEditor(parent);
		
		Canvas bottomBar = new Canvas(parent, SWT.NONE);
		bottomBar.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.END).grab(true, false).indent(0, 0).minSize(SWT.DEFAULT, 32).hint(SWT.DEFAULT, 32).create());		
		GridLayoutFactory.fillDefaults().numColumns(2).extendedMargins(8, 8, 3, 0).margins(0, 0).spacing(0, 0).generateLayout(bottomBar);
		
		// col1: buttons
		Button toggleDrawer = new Button(bottomBar, SWT.NONE | SWT.PUSH);
		((NSButton)toggleDrawer.view).setBezelStyle(OS.NSTexturedRoundedBezelStyle);
		toggleDrawer.setText("Drawer");
		toggleDrawer.setLayoutData(GridDataFactory.defaultsFor(toggleDrawer).align(SWT.BEGINNING, SWT.BEGINNING).indent(0, 0).create());
		toggleDrawer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				drawer.toggle();
			}
		});
	
		// col2: search
		Text searchField = new Text(bottomBar, SWT.SINGLE | SWT.SEARCH);
		searchField.setText("Search");
		searchField.setLayoutData(GridDataFactory.defaultsFor(searchField).align(SWT.END, SWT.BEGINNING).indent(0, 0).create());
		
		GridLayoutFactory.fillDefaults().extendedMargins(0, 0, 0, 0).spacing(0, 0).generateLayout(parent);
		return parent;
	}

	@Override
	protected Point getInitialSize() {
		if (restoreBounds())
			return previousSize;
		return new Point(500, 600);
	}
	
	@Override
	protected Point getInitialLocation(Point initialSize) {
		if (restoreBounds()) 
			return previousLocation;
		return super.getInitialLocation(initialSize);
	}
		
	private void saveState() {
	}


	private boolean restoreBounds() {
		return false;
	}

	
}
