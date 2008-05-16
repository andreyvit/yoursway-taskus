package com.mkalugin.corchy.internal.ui.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.cocoa.NSButton;
import org.eclipse.swt.internal.cocoa.NSRange;
import org.eclipse.swt.internal.cocoa.NSRect;
import org.eclipse.swt.internal.cocoa.NSSize;
import org.eclipse.swt.internal.cocoa.OS;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class CocoaUtil {

	public static NSSize NSMakeSize(float w, float h) {
		NSSize s = new NSSize();
		s.width = w;
		s.height = h;
		return s;		
	}

	public static NSRect NSMakeRect(float x, float y, float w, float h) {
		NSRect s = new NSRect();
		s.x = x;
		s.y = y;
		s.width = w;
		s.height = h;
		return s;		
	}
	
	public static NSRange NSMakeRange(int location, int length) {
		NSRange r = new NSRange();
		r.location = location;
		r.length = length;
		return r;
	}
	
	public static Button texturedButton(Composite parent) {
		Button button = new Button(parent, SWT.NONE | SWT.PUSH);
        ((NSButton) button.view).setBezelStyle(OS.NSTexturedRoundedBezelStyle);
        ((NSButton) button.view).setImagePosition(OS.NSImageOnly);
        return button;
	}
	
}
