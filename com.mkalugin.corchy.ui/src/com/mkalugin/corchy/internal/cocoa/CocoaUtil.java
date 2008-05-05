package com.mkalugin.corchy.internal.cocoa;

import org.eclipse.swt.internal.cocoa.NSRange;
import org.eclipse.swt.internal.cocoa.NSSize;

public class CocoaUtil {

	public static NSSize NSMakeSize(float w, float h) {
		NSSize s = new NSSize();
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
	
}
