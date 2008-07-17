package com.mkalugin.corchy.ui.controls;

import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public abstract class FileSheet extends FileDialog {

	public FileSheet(Shell parent, int style) {
		super(parent, style);
	}

	@Override
	protected void checkSubclass() {
	}
	
	@Override
	public String open() {
		String res = super.open();
		finished(res);
		return res;
	}

	protected abstract void finished(String result);

}
