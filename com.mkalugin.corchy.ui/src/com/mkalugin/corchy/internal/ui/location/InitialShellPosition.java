/**
 * 
 */
package com.mkalugin.corchy.internal.ui.location;

import org.eclipse.jface.util.Geometry;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

public enum InitialShellPosition {
    
    SYSTEM_DEFAULT {

        @Override
        public Point calculatePosition(Display display, Shell parent, Point initialSize) {
            return null;
        }
    },
    
    CENTERED {

        @Override
        public Point calculatePosition(Display display, Shell parent, Point initialSize) {
            Monitor monitor = display.getPrimaryMonitor();
            if (parent != null) {
                monitor = parent.getMonitor();
            }

            Rectangle monitorBounds = monitor.getClientArea();
            Point centerPoint;
            if (parent != null) {
                centerPoint = Geometry.centerPoint(parent.getBounds());
            } else {
                centerPoint = Geometry.centerPoint(monitorBounds);
            }

            return new Point(centerPoint.x - (initialSize.x / 2), Math.max(
                    monitorBounds.y, Math.min(centerPoint.y
                            - (initialSize.y * 2 / 3), monitorBounds.y
                            + monitorBounds.height - initialSize.y)));
        }
    
    };
    
    public abstract Point calculatePosition(Display display, Shell parent, Point initialSize);
    
}