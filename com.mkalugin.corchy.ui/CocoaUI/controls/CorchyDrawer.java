package com.mkalugin.corchy.internal.ui.drawer;

import static com.mkalugin.corchy.internal.ui.util.CocoaUtil.NSMakeSize;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.internal.cocoa.OS;
import org.eclipse.swt.widgets.Drawer;
import org.eclipse.swt.widgets.Shell;

import com.mkalugin.corchy.internal.ui.SwtCocoaOutlineView;

public class CorchyDrawer {

	private final Drawer drawer;
	private SwtCocoaOutlineView swtCocoaOutlineView;

	public CorchyDrawer(Shell host) {
		drawer = new Drawer(host, NSMakeSize(150, 0), OS.NSMinXEdge);
		drawer.setMinSize(NSMakeSize(30, 0));
		drawer.setMaxSize(NSMakeSize(250, 0));
		new SwtCocoaOutlineView(drawer);
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

	public SwtCocoaOutlineView swtCocoaOutlineView() {
		return swtCocoaOutlineView;
	}
	
}
