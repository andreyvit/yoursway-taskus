package com.mkalugin.corchy.internal.ui;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.cocoa.NSColor;
import org.eclipse.swt.internal.cocoa.NSTableView;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class OutlineView {

	private ListViewer viewer;

	public OutlineView(Composite parent) {
		createControl(parent);
	}

	private void createControl(Composite parent) {
		viewer = new ListViewer(parent, SWT.V_SCROLL);
		Control control = viewer.getControl();
		control.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL,
				SWT.FILL).grab(true, true).create());
		NSTableView tableView = (NSTableView) control.view;
		tableView.setBackgroundColor(NSColor.colorWithDeviceRed((float)209.0/255, (float)215.0/255, (float)226.0/255, 1));
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setLabelProvider(new LabelProvider());
		viewer.setInput(new String[] { "Pizda", "Hui" });		
	}

}
