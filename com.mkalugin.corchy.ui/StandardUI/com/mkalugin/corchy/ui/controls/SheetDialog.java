package com.mkalugin.corchy.ui.controls;

import org.eclipse.swt.widgets.Shell;

public abstract class SheetDialog {

	protected Shell dialog;
	private final Shell parent;

	public SheetDialog(Shell parent) {
		if (parent == null)
			throw new IllegalArgumentException("parent is null");

		this.parent = parent;
	}

	protected abstract Shell createShell();

	public void open() {
		dialog = createShell();
		if (parent == null)
			throw new NullPointerException("parent shell is null");

		dialog.open();
	}

	public void dismiss() {
		if (dialog != null && !dialog.isDisposed()) {
			parent.setActive();
			dialog.dispose();
		}
	}

}
