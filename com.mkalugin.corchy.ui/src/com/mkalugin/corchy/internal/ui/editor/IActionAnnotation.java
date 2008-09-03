package com.mkalugin.corchy.internal.ui.editor;

import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

public interface IActionAnnotation extends MouseListener, MouseTrackListener{

	void render(GC gc, Point offset);
	
	Point computeSize();
	
	void doAction();
	
	void dispose();
	
}
