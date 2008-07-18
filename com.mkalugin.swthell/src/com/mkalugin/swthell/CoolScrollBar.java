package com.mkalugin.swthell;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

public class CoolScrollBar extends Canvas {

	private float whole;
	private float visible;
	private float position;

	private float alpha;
	private Color color;
	
	private float beginMargin;
	private float endMargin;

	private boolean vertical = true;
	
	private boolean runningAnimation = false;
	private Thread animationThread;
	private AnimationRunnable animationRunnable;
	private Object animationLock = new Object();
	protected Rectangle lastRunnerRect;

	private class DragginMouseListener implements MouseListener, MouseMoveListener {

		private boolean dragMode;

		public void mouseDoubleClick(MouseEvent e) {
		}

		public void mouseDown(MouseEvent e) {
			if (lastRunnerRect != null) {
				dragMode = true;
				if (!lastRunnerRect.contains(e.x, e.y)) {
					float newPosition = positionForClick(e.x, e.y);
					if (newPosition >= 0) {
						position = newPosition;
						redraw();
						CoolScrollBar.this.notifyListeners(SWT.Selection, new Event());
					}
				}
			}
		}

		public void mouseUp(MouseEvent e) {
			dragMode = false;
		}

		public void mouseMove(MouseEvent e) {
			if (dragMode) {
				float newPosition = positionForClick(e.x, e.y);
				if (newPosition >= 0) {
					position = newPosition;
					redraw();
					CoolScrollBar.this.notifyListeners(SWT.Selection, new Event());
				}
			}
		}

	}

	private Rectangle calculateRunnerRect() {
		float ratio = visible / whole;
		if (ratio > 0.9) // nothing to show
			return null;
		Rectangle clientArea = CoolScrollBar.this.getClientArea();
		int length = vertical?clientArea.height:clientArea.width;
		int runnerLength = (int) Math.max(ratio * length, 10);
		int activeSpace = (int) (length - runnerLength - beginMargin - endMargin);
		int pos = (int) (activeSpace * (position / (whole - visible)) + beginMargin);
		if (vertical)
			return new Rectangle(3, pos, 7, runnerLength);
		else
			return new Rectangle(pos, 3, runnerLength, 7);
	}

	private float positionForClick(float xc, float yc) {
		float coord = vertical?yc:xc;
		
		float ratio = visible / whole;
		if (ratio > 0.9) // nothing to show
			return -1;

		Rectangle clientArea = CoolScrollBar.this.getClientArea();
		float length = vertical?clientArea.height:clientArea.width;
		float runnerLength = Math.max(ratio * length, 10);
		float activeSpace = length - runnerLength - beginMargin - endMargin;

		if (coord < beginMargin)
			coord = beginMargin;

		if (coord > beginMargin + activeSpace)
			coord = beginMargin + activeSpace;

		return (whole - visible) * (coord - beginMargin) / activeSpace;
	}

	public CoolScrollBar(Composite parent, int style, boolean vertical) {
		super(parent, style);
		this.vertical = vertical;
		alpha = 0;
		beginMargin = 6;
		endMargin = 6;
		color = new Color(Display.getDefault(), 80, 80, 80);
		this.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		addPaintListener(new PaintListener() {

			public void paintControl(PaintEvent e) {
				Rectangle runnerRect = calculateRunnerRect();
				if (runnerRect != null) {
					GC gc = e.gc;
					gc.setAlpha((int) (alpha * 255));
					gc.setBackground(color);
					gc.fillRoundRectangle(runnerRect.x, runnerRect.y, runnerRect.width,
							runnerRect.height, 7, 7);
				}
				lastRunnerRect = runnerRect;
			}

		});
		addDisposeListener(new DisposeListener() {

			public void widgetDisposed(DisposeEvent e) {
				color.dispose();
			}

		});
		DragginMouseListener dragListener = new DragginMouseListener();
		addMouseListener(dragListener);
		addMouseMoveListener(dragListener);
//		addMouseWheelListener(new MouseWheelListener() {
//
//			public void mouseScrolled(MouseEvent e) {
//				scrollable.scrollBy(e.count);
//				redraw();
//			}
//
//		});
		animationRunnable = new AnimationRunnable(true, 300);
		animationThread = new Thread(animationRunnable);
	}

	public void setRunnerSize(float whole, float visible) {
		this.whole = whole;
		this.visible = visible;
		if (position > whole - visible)
			position = 0;
		redraw();
	}
	
	public void setPosition(float position) {
//		if (position < 0 || position > whole - visible)
//			throw new IllegalArgumentException("position is out of bounds");
		if (position < 0)
			position = 0;
		if (position > whole - visible)
			position = whole - visible;
		this.position = position;
		redraw();
	}
	
	public float getPosition() {
		return position;
	}

	
	public float beginMargin() {
		return beginMargin;
	}
	
	public float endMargin() {
		return endMargin;
	}
	
	public void setBeginMargin(float beginMargin) {
		this.beginMargin = beginMargin;
	}
	
	public void setEndMargin(float endMargin) {
		this.endMargin = endMargin;
	}
	
	@Override
	public Point computeSize(int hint, int hint2, boolean changed) {
		Point computedSize = super.computeSize(hint, hint2, changed);
		if (vertical)
			return new Point(14, computedSize.y);
		else
			return new Point(computedSize.x, 14);
	}

	// animation stuff

	private class AnimationRunnable implements Runnable {

		private boolean show;
		private float time;

		public AnimationRunnable(boolean show, float time) {
			this.show = show;
			this.time = time;
		}

		public void run() {
			runningAnimation = true;

			long delay = 5;
			float step = delay / time;

			while (((show && alpha < 1) || (!show && alpha > 0))) {
				synchronized (animationLock) {
					if (!runningAnimation)
						break;
					Display.getDefault().syncExec(new Runnable() {

						public void run() {
							if (!CoolScrollBar.this.isDisposed())
								CoolScrollBar.this.redraw();
						}

					});
					try {
						Thread.sleep(delay);
					} catch (InterruptedException e) {
						e.printStackTrace();
						break;
					}
					if (show)
						alpha += step;
					else
						alpha -= step;
				}
			}
			if (show && alpha > 1)
				alpha = 1;
			if (!show && alpha < 0)
				alpha = 0;
			runningAnimation = false;
		}

	}

	private synchronized void animateAlpha(final boolean show) {
		new Thread(new Runnable() {

			public void run() {
				synchronized (animationLock) {
					animationRunnable.show = show;
					if (!runningAnimation) {
						animationThread = new Thread(animationRunnable);
						animationThread.start();
					}
				}
			}

		}).start();
	}

	public void animateShow() {
		animateAlpha(true);
	}

	public void animateHide() {
		animateAlpha(false);
	}

}
