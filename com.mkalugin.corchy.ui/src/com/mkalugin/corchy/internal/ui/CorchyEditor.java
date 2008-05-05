package com.mkalugin.corchy.internal.ui;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;

public class CorchyEditor {

	private StyledText styledText;

	public CorchyEditor(Composite parent) {
		createControls(parent);
	}

	private void createControls(Composite parent) {
		styledText = new StyledText(parent, SWT.BORDER | SWT.MULTI
				| SWT.V_SCROLL | SWT.WRAP);
		styledText.setText("Hello, World!\nYo!");
		styledText.setLayoutData(GridDataFactory.fillDefaults().grab(true, true)
				.align(SWT.FILL, SWT.FILL).create());
	}

	public void dispose() {
		styledText.dispose();
	}

}
