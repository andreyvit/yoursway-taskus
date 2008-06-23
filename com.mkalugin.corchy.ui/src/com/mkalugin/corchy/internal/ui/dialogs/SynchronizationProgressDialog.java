package com.mkalugin.corchy.internal.ui.dialogs;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

public class SynchronizationProgressDialog extends SheetDialog {

	private Label label;

	public SynchronizationProgressDialog(Shell parent) {
		super(parent);
	}

	@Override
	protected Shell createShell() {
		Shell dialog = new Shell();		
		dialog.setSize(350, 100);

		label = new Label(dialog, SWT.NONE);
		label.setText("Synchronizing...");		
		label.setLayoutData(GridDataFactory.swtDefaults().minSize(300, SWT.DEFAULT).hint(300, SWT.DEFAULT).create());

		ProgressBar progressBar = new ProgressBar(dialog, SWT.INDETERMINATE);
		progressBar.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		GridLayoutFactory.swtDefaults().extendedMargins(30, 30, 20, 30).spacing(8, 8).margins(0, 0)
				.generateLayout(dialog);
		return dialog;
	}
	
	public void setText(String text) {
		label.setText(text);
	}

}
