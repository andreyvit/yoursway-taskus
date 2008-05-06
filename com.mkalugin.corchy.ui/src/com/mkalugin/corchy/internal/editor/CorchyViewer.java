/**
 * 
 */
package com.mkalugin.corchy.internal.editor;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;

class CorchyViewer extends SourceViewer {
	
	CorchyViewer(Composite parent) {
		super(parent, null, SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
	}

	@Override
	protected StyledText createTextWidget(Composite parent, int styles) {
		StyledText styledText = super.createTextWidget(parent, styles);
		styledText.setIndent(15);
		styledText.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL,
				SWT.FILL).create());
		return styledText;
	}

	@Override
	protected Layout createLayout() {
		return GridLayoutFactory.fillDefaults().extendedMargins(0, 0, 0, 0).create();
	}
	
}