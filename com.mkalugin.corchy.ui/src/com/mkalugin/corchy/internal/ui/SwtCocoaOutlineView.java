package com.mkalugin.corchy.internal.ui;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.cocoa.NSColor;
import org.eclipse.swt.internal.cocoa.NSOutlineView;
import org.eclipse.swt.widgets.Composite;

import com.mkalugin.pikachu.core.controllers.viewglue.OutlineView;
import com.mkalugin.pikachu.core.controllers.viewglue.OutlineViewCallback;

public class SwtCocoaOutlineView implements OutlineView {

	private TreeViewer viewer;
	private ITreeContentProvider contentProvider;
    private OutlineViewCallback callback;

	public SwtCocoaOutlineView(Composite parent) {
		createControl(parent);
//		Workspace workspace = CorchyApplication.workspace();
//		workspace.registerConsumer(this);
	}

	private void createControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.V_SCROLL);
		NSOutlineView tableView = (NSOutlineView) viewer.getControl().view;
		tableView.setBackgroundColor(NSColor.colorWithDeviceRed((float) 209.0 / 255,
				(float) 215.0 / 255, (float) 226.0 / 255, 1));
		contentProvider = new ITreeContentProvider() {

			public Object[] getChildren(Object parentElement) {
//				if (parentElement instanceof Project)
//					return ((Project) parentElement).getTasks();
				return new Object[0];
			}

			public Object getParent(Object element) {
				return null;
			}

			public boolean hasChildren(Object element) {
				return getChildren(element).length > 0;
			}

			public Object[] getElements(Object inputElement) {
//				if (inputElement instanceof WorkspaceSnapshot) {
//					return ((WorkspaceSnapshot) inputElement).projects();
//				}
				return new Object[0];
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

		};
		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(new LabelProvider());
	}

	public void setLayoutData(Object outlineData) {
		viewer.getControl().setLayoutData(outlineData);
	}

    public OutlineView bind(OutlineViewCallback callback) {
        if (callback == null)
            throw new NullPointerException("callback is null");
        if (this.callback != null)
            throw new IllegalStateException("callback is already set");
        this.callback = callback;
        return this;
    }

//	public void consume(final WorkspaceSnapshot snapshot) {
//		Display.getDefault().asyncExec(new Runnable() {
//
//			public void run() {
//				viewer.setInput(snapshot);
//				viewer.refresh();
//			}
//
//		});
//
//	}

}