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

public class CorchyViewer extends SourceViewer {
    
    private static final String VIEWER_KEY = "textViewer";
    
    public final static CorchyViewer fromControl(StyledText control) {
        Object data = control.getData(VIEWER_KEY);
        if (data instanceof CorchyViewer)
            return (CorchyViewer) data;
        return null;
    }
	
	private StyledText styledText;

	CorchyViewer(Composite parent) {
		super(parent, null, SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
		configure(new CorchySourceViewerConfiguration());
	}

	@Override
	protected StyledText createTextWidget(Composite parent, int styles) {
		styledText = super.createTextWidget(parent, styles);
		styledText.setIndent(15);
		styledText.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL,
				SWT.FILL).create());
		styledText.setData(VIEWER_KEY, this);
		return styledText;
	}

	@Override
	protected Layout createLayout() {
		return GridLayoutFactory.fillDefaults().extendedMargins(0, 0, 0, 0).create();
	}
	
}