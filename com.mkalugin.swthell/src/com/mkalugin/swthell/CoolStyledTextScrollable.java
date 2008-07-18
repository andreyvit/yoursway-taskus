package com.mkalugin.swthell;

import static com.mkalugin.swthell.CompositeUtils.addAllChildrenListener;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class CoolStyledTextScrollable extends Composite {

	private final StyledText styledText;
	private CoolScrollBar vScrollBar;
	private int widgetHeight;
	private int textHeight;
	private int topPixel;

	public CoolStyledTextScrollable(Composite parent, StyledText styledText) {
		super(parent, SWT.NONE);
		if (styledText == null)
			throw new IllegalArgumentException("styledText is null");
		styledText.setParent(this);
		this.styledText = styledText;
		setupControls();
	}

	private void setupControls() {
		styledText.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL,
				SWT.FILL).create());

		vScrollBar = new CoolScrollBar(this, SWT.NONE, true);
		vScrollBar.setLayoutData(GridDataFactory.fillDefaults().grab(false, true).create());

		GridLayoutFactory.fillDefaults().numColumns(2).spacing(1, 0).generateLayout(this);
		this.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		installListener();
	}

	public CoolStyledTextScrollable(Composite parent, int style) {
		super(parent, SWT.NONE);
		this.styledText = new StyledText(this, style);
		setupControls();
	}
	
	public StyledText styledText() {
		return styledText;
	}

	private void installListener() {
		styledText.addMouseWheelListener(new MouseWheelListener() {

			public void mouseScrolled(MouseEvent e) {
				vScroll(e.count, true);
			}

		});
		Listener updateListener = new Listener() {

			public void handleEvent(Event event) {
				widgetHeight = styledText.getBounds().height;
				int charCount = styledText.getCharCount();
				if (charCount > 0)
					textHeight = styledText.getTextBounds(0, charCount - 1).height;
				else
					textHeight = 0;
				topPixel = styledText.getTopPixel();
				vScrollBar.setRunnerSize(textHeight, widgetHeight);
				vScrollBar.setPosition(topPixel);
			}

		};
		styledText.addListener(SWT.Modify, updateListener);
		styledText.addListener(SWT.Traverse, updateListener);
		styledText.addListener(SWT.Resize, updateListener);
		new MouseEnterExitTracker(this, new Listener() {

			public void handleEvent(Event event) {
				if (event.type == SWT.MouseEnter) {
					vScrollBar.animateShow();
				} else if (event.type == SWT.MouseExit) {
					vScrollBar.animateHide();
				}

			}

		});
		addAllChildrenListener(this, SWT.MouseMove, new Listener() {

			public void handleEvent(Event event) {
				vScrollBar.animateShow();
			}

		});
		vScrollBar.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				topPixel = (int) vScrollBar.getPosition();
				styledText.setTopPixel(topPixel);
			}

		});
	}

	public void vScroll(int count, boolean notify) {
		topPixel -= 5 * count;
		if (topPixel < 0)
			topPixel = 0;
		if (topPixel > textHeight - widgetHeight)
			topPixel = textHeight - widgetHeight;
		styledText.setTopPixel(topPixel);
		vScrollBar.setPosition(topPixel);
	}

}
