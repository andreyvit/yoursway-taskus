package com.mkalugin.corchy.internal.ui;

import static com.mkalugin.corchy.internal.images.CorchyImages.IMG_BOTTOM_BAR;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public abstract class MainWindow extends Window {

	private static final String KEY_WIDTH = "width";
	private static final String KEY_HEIGHT = "height";
	private static final String KEY_Y = "y";
	private static final String KEY_X = "x";
	
	private final boolean hasBottomBar;
	
	private IDialogSettings dialogSettings;
	
	public MainWindow(boolean hasBottomBar) {
		super((Shell) null);
		this.hasBottomBar = hasBottomBar;
	}

	protected abstract String title();
	
	protected abstract void dispose();
	
	protected abstract String dialogId();

	protected void createControls(Composite parent) {		
	}
	
	protected void fillBottomBar(Composite bottomBar) {
	}
	
	@Override
	protected void configureShell(Shell shell) {
		shell.setText(title());
		shell.addListener(SWT.Dispose, new Listener() {
			public void handleEvent(Event event) {
				dispose();
				saveState();
			}
		});
		GridLayoutFactory.fillDefaults().extendedMargins(0, 0, 0, 0).spacing(0, 0)
				.generateLayout(shell);
	}
	
	@Override
	protected final Control createContents(Composite parent) {		
		createControls(parent);
		if (hasBottomBar)
			createBottomBar(parent);
		GridLayoutFactory.fillDefaults().extendedMargins(0, 0, 0, 0).spacing(0,
				0).numColumns(1).generateLayout(parent);
		return parent;
	}
	
	private void createBottomBar(Composite parent) {
		final Canvas bottomBar = new Canvas(parent, SWT.NONE);
		bottomBar.addPaintListener(new PaintListener() {

			public void paintControl(PaintEvent e) {
				GC gc = e.gc;
				gc.setClipping((Rectangle)null);
				Pattern bottomBarPattern = new Pattern(Display.getCurrent(), IMG_BOTTOM_BAR.get());
				gc.setBackgroundPattern(bottomBarPattern);
				gc.fillRectangle(bottomBar.getClientArea());
			}
			
		});
		bottomBar.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL,
				SWT.END).grab(true, false).indent(0, 0)
				.minSize(SWT.DEFAULT, 32).hint(SWT.DEFAULT, 32).create());

		fillBottomBar(bottomBar);

		GridLayoutFactory.fillDefaults().numColumns(2).extendedMargins(8, 8, 4, 0).margins(0, 0)
				.spacing(0, 0).generateLayout(bottomBar);
	}

	@Override
	protected Point getInitialSize() {
		loadDialogSettings();
		try {
			int width = dialogSettings.getInt(KEY_WIDTH);
			int height = dialogSettings.getInt(KEY_HEIGHT);
			return new Point(width, height);
		} catch (NumberFormatException e) {
			return new Point(500, 600);
		}
	}

	@Override
	protected Point getInitialLocation(Point initialSize) {
		loadDialogSettings();
		try {
			int x = dialogSettings.getInt(KEY_X);
			int y = dialogSettings.getInt(KEY_Y);
			return new Point(x, y);
		} catch (NumberFormatException e) {
			return super.getInitialLocation(initialSize);
		}
	}

	private void saveState() {
		loadDialogSettings();
		Rectangle bounds = getShell().getBounds();
		dialogSettings.put(KEY_X, bounds.x);
		dialogSettings.put(KEY_Y, bounds.y);
		dialogSettings.put(KEY_WIDTH, bounds.width);
		dialogSettings.put(KEY_HEIGHT, bounds.height);
	}

	private void loadDialogSettings() {
		if (dialogSettings != null)
			return;
		dialogSettings = CorchyUIPlugin.instance().getDialogSettings().getSection(dialogId());
		if (dialogSettings == null) {
			dialogSettings = CorchyUIPlugin.instance().getDialogSettings().addNewSection(dialogId());			
		}
	}

	
}
