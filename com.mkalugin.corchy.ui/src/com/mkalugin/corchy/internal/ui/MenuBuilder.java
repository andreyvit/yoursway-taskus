package com.mkalugin.corchy.internal.ui;

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
	
	public MenuItem item(String title, int accel, final Runnable action) {
		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText(title);
		if (accel >= 0)
			item.setAccelerator(accel);
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
	
}
