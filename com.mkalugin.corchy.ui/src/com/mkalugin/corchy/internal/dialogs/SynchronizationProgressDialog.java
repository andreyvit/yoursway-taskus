package com.mkalugin.corchy.internal.dialogs;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

public class SynchronizationProgressDialog extends SheetDialog {

	public SynchronizationProgressDialog(Shell parent) {
		super(parent);
	}

	@Override
	protected Shell createShell() {
		Shell dialog = new Shell();		
		dialog.setSize(350, 100);
		dialog.setVisible(false);

		Label label = new Label(dialog, SWT.NONE);
		label.setText("Synchronizing...");
		label.setLayoutData(GridDataFactory.swtDefaults().create());

		ProgressBar progressBar = new ProgressBar(dialog, SWT.INDETERMINATE);
		progressBar.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		GridLayoutFactory.swtDefaults().extendedMargins(30, 30, 20, 30).spacing(8, 8).margins(0, 0)
				.generateLayout(dialog);
		return dialog;
	}

}
