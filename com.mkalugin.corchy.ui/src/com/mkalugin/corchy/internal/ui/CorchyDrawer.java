package com.mkalugin.corchy.internal.ui;

import static com.mkalugin.corchy.internal.cocoa.CocoaUtil.NSMakeSize;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.internal.cocoa.OS;
import org.eclipse.swt.widgets.Drawer;
import org.eclipse.swt.widgets.Shell;

public class CorchyDrawer {

	private final Drawer drawer;
	private OutlineView outlineView;

	public CorchyDrawer(Shell host) {
		drawer = new Drawer(host, NSMakeSize(150, 0), OS.NSMinXEdge);
		drawer.setMinSize(NSMakeSize(30, 0));
		drawer.setMaxSize(NSMakeSize(250, 0));
		new OutlineView(drawer);
		GridLayoutFactory.fillDefaults().extendedMargins(0, 0, 0, 0).generateLayout(drawer);
	}
	
	public void open() {
		drawer.open();
	}
		
	public void dispose() {
		drawer.dispose();
	}

	public void toggle() {
		drawer.toggle();
	}

	public OutlineView outlineView() {
		return outlineView;
	}
	
}
