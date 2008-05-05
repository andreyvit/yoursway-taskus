package com.mkalugin.corchy.internal.ui;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;

public class CorchyEditor {

	private Composite composite;
	private StyledText styledText;

	public CorchyEditor(Composite parent) {
		createControls(parent);
	}

	private void createControls(Composite parent) {
		composite = new Composite(parent, SWT.NULL);
		GridLayoutFactory.fillDefaults().extendedMargins(0, 0, 0, 0).applyTo(composite);
		styledText = new StyledText(composite, SWT.MULTI
				| SWT.V_SCROLL | SWT.WRAP);
		styledText.setText("Hello, World!\nYo!");
		styledText.setIndent(15);
		styledText.setLayoutData(GridDataFactory.fillDefaults().grab(true, true)
				.align(SWT.FILL, SWT.FILL).create());
	}

	public void dispose() {
		composite.dispose();
	}

	public void setLayoutData(Object editorData) {
		composite.setLayoutData(editorData);
	}

}
