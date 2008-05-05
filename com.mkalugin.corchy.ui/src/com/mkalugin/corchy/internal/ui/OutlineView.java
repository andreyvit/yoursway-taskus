package com.mkalugin.corchy.internal.ui;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
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
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setLabelProvider(new LabelProvider());
		viewer.setInput(new String[] { "Pizda", "Hui" });
	}

}
