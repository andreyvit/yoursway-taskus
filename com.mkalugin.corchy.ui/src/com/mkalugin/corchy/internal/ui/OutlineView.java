package com.mkalugin.corchy.internal.ui;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.cocoa.NSColor;
import org.eclipse.swt.internal.cocoa.NSTableView;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.mkalugin.corchy.ui.core.CorchyApplication;
import com.mkalugin.pikachu.core.model.ModelConsumer;
import com.mkalugin.pikachu.core.workspace.Workspace;
import com.mkalugin.pikachu.core.workspace.WorkspaceSnapshot;

public class OutlineView implements ModelConsumer<WorkspaceSnapshot> {

	private ListViewer viewer;
	private ArrayContentProvider contentProvider;

	public OutlineView(Composite parent) {
		createControl(parent);
		Workspace workspace = CorchyApplication.workspace();
		workspace.registerConsumer(this);
	}

	private void createControl(Composite parent) {
		viewer = new ListViewer(parent, SWT.V_SCROLL);
		NSTableView tableView = (NSTableView) viewer.getControl().view;
		tableView.setBackgroundColor(NSColor.colorWithDeviceRed((float) 209.0 / 255,
				(float) 215.0 / 255, (float) 226.0 / 255, 1));
		contentProvider = new ArrayContentProvider();
		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(new LabelProvider());
	}

	public void setLayoutData(Object outlineData) {
		viewer.getControl().setLayoutData(outlineData);
	}

	public void consume(final WorkspaceSnapshot snapshot) {
		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				String[] ast = snapshot.ast();
				viewer.setInput(ast);
				viewer.refresh();
			}

		});

	}

}
