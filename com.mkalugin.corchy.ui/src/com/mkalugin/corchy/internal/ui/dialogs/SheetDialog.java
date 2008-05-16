package com.mkalugin.corchy.internal.ui.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.Callback;
import org.eclipse.swt.internal.cocoa.NSApplication;
import org.eclipse.swt.internal.cocoa.NSWindow;
import org.eclipse.swt.internal.cocoa.OS;
import org.eclipse.swt.widgets.Shell;

public abstract class SheetDialog {

	public static final int sel_sheetDidEnd_returnCode_contextInfo_ = OS
			.sel_registerName("sheetDidEnd:returnCode:contextInfo:");

	private static void initClass() {
		Callback callback = new Callback(SheetDialog.class, "delegateProc", 5);
		int proc = callback.getAddress();
		if (proc == 0)
			SWT.error(SWT.ERROR_NO_MORE_CALLBACKS);

		String className = "SWTProgressDelegate";
		int cls = OS.objc_allocateClassPair(OS.class_NSObject, className, 0);
		OS.class_addIvar(cls, "tag", OS.PTR_SIZEOF, (byte) (Math.log(OS.PTR_SIZEOF) / Math.log(2)),
				"i");
		OS.class_addMethod(cls, sel_sheetDidEnd_returnCode_contextInfo_, proc, "@:@ii");
		OS.objc_registerClassPair(cls);
	}

	static {
		initClass();
	}

	Shell dialog;
	private final Shell parent;
	private NSApplication application;
	private int session;

	public SheetDialog(Shell parent) {
		if (parent == null)
			throw new IllegalArgumentException("parent is null");

		this.parent = parent;
		application = parent.getDisplay().getApplication();
		
		dialog = createShell();
	}
	
	protected abstract Shell createShell();

	public void open() {
		if (parent == null)
			throw new NullPointerException("parent shell is null");

		SWTSheetDelegate delegate = (SWTSheetDelegate) new SWTSheetDelegate().alloc()
				.init();
		int ref = OS.NewGlobalRef(this);
		if (ref == 0)
			SWT.error(SWT.ERROR_NO_HANDLES);
		delegate.setTag(ref);

		NSWindow sheetWindow = dialog.view.window();
		sheetWindow.setReleasedWhenClosed(false);
		application.beginSheet(sheetWindow, parent.view.window(), delegate,
				sel_sheetDidEnd_returnCode_contextInfo_, 0);
	}

	static int delegateProc(int id, int sel, int arg0, int arg1, int arg2) {
		SWTSheetDelegate delegate = new SWTSheetDelegate(id);
		int ref = delegate.tag();
		OS.DeleteGlobalRef(ref);
		return 0;
	}

	public void dismiss() {
		application.endSheet_(dialog.view.window());
		dialog.view.window().close();
		parent.setActive();
	}

}
