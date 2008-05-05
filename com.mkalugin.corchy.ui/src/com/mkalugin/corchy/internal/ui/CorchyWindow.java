package com.mkalugin.corchy.internal.ui;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.cocoa.NSButton;
import org.eclipse.swt.internal.cocoa.OS;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class CorchyWindow extends Window {

	private static String APP_NAME = "Corchy";

	private CorchyEditor editor;

	private Point previousLocation = null;
	private Point previousSize = null;

	private OutlineView outlineView;

	public CorchyWindow() {
		super((Shell) null);
	}

	@Override
	protected void configureShell(Shell shell) {
		shell.setText(APP_NAME);
		shell.addListener(SWT.Dispose, new Listener() {
			public void handleEvent(Event event) {
				editor.dispose();
				saveState();
			}
		});
		GridLayoutFactory.fillDefaults().extendedMargins(0, 0, 0, 0)
				.generateLayout(shell);
	}
	
	private void createEditorAndOutline(final Composite parent) {
		outlineView = new OutlineView(parent);
		editor = new CorchyEditor(parent);
		final Sash sash = new Sash (parent, SWT.VERTICAL);
		
		final FormLayout form = new FormLayout ();
		form.spacing = 0;
		parent.setLayout (form);
		
		FormData outlineData = new FormData ();
		outlineData.left = new FormAttachment (0, 0);
		outlineData.right = new FormAttachment (sash, 0);
		outlineData.top = new FormAttachment (0, 0);
		outlineData.bottom = new FormAttachment (100, 0);
		outlineView.setLayoutData (outlineData);
		
		final int limit = 100, percent = 30; // min 100 px. for the outline, 30% initially
		final FormData sashData = new FormData ();
		sashData.left = new FormAttachment (percent, 0);
		sashData.top = new FormAttachment (0, 0);
		sashData.bottom = new FormAttachment (100, 0);
		sashData.width = 3;
		sash.setLayoutData (sashData);
		sash.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event e) {
				Rectangle sashRect = sash.getBounds ();
				Rectangle shellRect = parent.getClientArea ();
				int right = shellRect.width - sashRect.width - limit;
				e.x = Math.max (Math.min (e.x, right), limit);
				if (e.x != sashRect.x)  {
					sashData.left = new FormAttachment (0, e.x);
					parent.layout ();
				}
			}
		});
		sash.setCursor(new Cursor(Display.getCurrent(), SWT.CURSOR_SIZEWE));
		
		FormData editorData = new FormData ();
		editorData.left = new FormAttachment (sash, 0);
		editorData.right = new FormAttachment (100, 0);
		editorData.top = new FormAttachment (0, 0);
		editorData.bottom = new FormAttachment (100, 0);
		editor.setLayoutData (editorData);
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite editorBlock = new Composite(parent, SWT.NULL);
		createEditorAndOutline(editorBlock);
		editorBlock.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).create());

		Canvas bottomBar = new Canvas(parent, SWT.BORDER);
		bottomBar.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL,
				SWT.END).grab(true, false).indent(0, 0)
				.minSize(SWT.DEFAULT, 32).hint(SWT.DEFAULT, 32).span(2, 1).create());
		GridLayoutFactory.fillDefaults().numColumns(2).extendedMargins(8, 8, 3,
				0).margins(0, 0).spacing(0, 0).generateLayout(bottomBar);

		// col1: buttons
		Button toggleDrawer = new Button(bottomBar, SWT.NONE | SWT.PUSH);
		((NSButton) toggleDrawer.view)
				.setBezelStyle(OS.NSTexturedRoundedBezelStyle);
		toggleDrawer.setText("Button");		
		toggleDrawer.setLayoutData(GridDataFactory.defaultsFor(toggleDrawer)
				.align(SWT.BEGINNING, SWT.BEGINNING).indent(0, 0).create());
		toggleDrawer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});

		// col2: search
		Text searchField = new Text(bottomBar, SWT.SINGLE | SWT.SEARCH);
		searchField.setText("Search");
		searchField.setLayoutData(GridDataFactory.defaultsFor(searchField)
				.align(SWT.END, SWT.BEGINNING).indent(0, 0).create());

		GridLayoutFactory.fillDefaults().extendedMargins(0, 0, 0, 0).spacing(0,
				0).numColumns(2).generateLayout(parent);
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
