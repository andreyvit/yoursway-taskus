package com.mkalugin.corchy.internal.ui;

import static com.mkalugin.corchy.internal.images.CorchyImages.ICN_SYNC;

import java.io.File;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.cocoa.NSButton;
import org.eclipse.swt.internal.cocoa.OS;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.mkalugin.corchy.internal.editor.CorchyEditor;
import com.mkalugin.corchy.ui.core.CorchyApplication;
import com.mkalugin.pikachu.core.model.ModelConsumer;
import com.mkalugin.pikachu.core.storage.StorageException;
import com.mkalugin.pikachu.core.storage.file.FSDataStorage;
import com.mkalugin.pikachu.core.workspace.WorkspaceSnapshot;

public class CorchyWindow extends MainWindow implements ModelConsumer<WorkspaceSnapshot> {

	private static final String DIALOG_ID = "mainWindow";

	private static final String APP_TITLE = "Corchy";

	private static final String EXT = ".corchy";

	private CorchyEditor editor;
	private OutlineView outlineView;
	private String title = null;

	public CorchyWindow() {
		super(true);
		CorchyApplication.workspace().registerConsumer(this);
	}

	@Override
	protected void dispose() {
		try {
			CorchyApplication.workspace().flush();
		} catch (StorageException e) {
		}
		CorchyApplication.saveLastStorageInfo();
	}

	@Override
	protected String title() {
		if (title != null)
			return title;
		return APP_TITLE;
	}

	private void createEditorAndOutline(final Composite parent) {
		outlineView = new OutlineView(parent);
		editor = new CorchyEditor(parent);
		final Sash sash = new Sash(parent, SWT.VERTICAL);

		final FormLayout form = new FormLayout();
		form.spacing = 0;
		parent.setLayout(form);

		FormData outlineData = new FormData();
		outlineData.left = new FormAttachment(0, 0);
		outlineData.right = new FormAttachment(sash, 0);
		outlineData.top = new FormAttachment(0, 0);
		outlineData.bottom = new FormAttachment(100, 0);
		outlineView.setLayoutData(outlineData);

		// min 100 px. for the outline,
		// 30% initially
		final int limit = 100, percent = 30;
		final FormData sashData = new FormData();
		sashData.left = new FormAttachment(percent, 0);
		sashData.top = new FormAttachment(0, 0);
		sashData.bottom = new FormAttachment(100, 0);
		sashData.width = 2;
		sash.setLayoutData(sashData);
		sash.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				Rectangle sashRect = sash.getBounds();
				Rectangle shellRect = parent.getClientArea();
				int right = shellRect.width - sashRect.width - limit;
				e.x = Math.max(Math.min(e.x, right), limit);
				if (e.x != sashRect.x) {
					sashData.left = new FormAttachment(0, e.x);
					parent.layout();
				}
			}
		});

		FormData editorData = new FormData();
		editorData.left = new FormAttachment(sash, 0);
		editorData.right = new FormAttachment(100, 0);
		editorData.top = new FormAttachment(0, 0);
		editorData.bottom = new FormAttachment(100, 0);
		editor.setLayoutData(editorData);
	}

	Menu createEditMenu(Shell shell) {
		Menu bar = shell.getMenuBar();
		Menu menu = new Menu(bar);

		MenuBuilder builder = new MenuBuilder(menu);

		builder.item("Undo", SWT.MOD1 + 'Z', new Runnable() {
			public void run() {
				if (editor.isActive()) {
					editor.undo();
				}
			}
		});
		builder.item("Redo", SWT.MOD1 + 'Y', new Runnable() {
			public void run() {
				if (editor.isActive()) {
					editor.redo();
				}
			}
		});
		builder.separator();
		builder.item("Cut", SWT.MOD1 + 'X', new Runnable() {
			public void run() {
				Control focusControl = Display.getCurrent().getFocusControl();
				if (focusControl instanceof StyledText) {
					((StyledText) focusControl).cut();
				}
				if (focusControl instanceof Text) {
					((Text) focusControl).cut();
				}
			}
		});
		builder.item("Copy", SWT.MOD1 + 'C', new Runnable() {
			public void run() {
				Control focusControl = Display.getCurrent().getFocusControl();
				if (focusControl instanceof StyledText) {
					((StyledText) focusControl).copy();
				}
				if (focusControl instanceof Text) {
					((Text) focusControl).copy();
				}
			}
		});
		builder.item("Paste", SWT.MOD1 + 'V', new Runnable() {
			public void run() {
				Control focusControl = Display.getCurrent().getFocusControl();
				if (focusControl instanceof StyledText) {
					((StyledText) focusControl).paste();
				}
				if (focusControl instanceof Text) {
					((Text) focusControl).paste();
				}
			}
		});
		builder.item("Select All", SWT.MOD1 + 'A', new Runnable() {
			public void run() {
				Control focusControl = Display.getCurrent().getFocusControl();
				if (focusControl instanceof StyledText) {
					((StyledText) focusControl).selectAll();
				}
				if (focusControl instanceof Text) {
					((Text) focusControl).selectAll();
				}
			}
		});

		return menu;
	}

	Menu createFileMenu(final Shell shell) {
		Menu bar = shell.getMenuBar();
		Menu menu = new Menu(bar);

		MenuBuilder builder = new MenuBuilder(menu);
		builder.item("Open...", SWT.MOD1 + 'O', new Runnable() {
			public void run() {
				DirectoryDialog fileDialog = new DirectoryDialog(getShell(), SWT.OPEN);
				while (true) {
					String result = fileDialog.open();
					if (result == null)
						break;
					try {
						FSDataStorage dataStorage = new FSDataStorage(new File(result), false);
						CorchyApplication.openWorkspaceWithStorage(dataStorage);
						break;
					} catch (Exception e) {
						MessageBox messageBox = new MessageBox(getShell(), SWT.OK | SWT.CANCEL);
						messageBox.setMessage("Impossible to open");
						messageBox.setText("Failed to open file " + result + " due to:\n"
								+ e.getLocalizedMessage()
								+ "\nPlease select other file or click 'cancel'.");
						int open = messageBox.open();
						if (open == SWT.CANCEL)
							break;
					}
				}
			}
		});
		builder.item("Save as...", SWT.MOD1 + SWT.SHIFT + 'S', new Runnable() {
			public void run() {
				FileDialog fileDialog = new FileDialog(getShell(), SWT.SAVE);
				fileDialog.setFilterExtensions(new String[] { EXT });
				fileDialog.setFileName("Unnamed." + EXT);
				while (true) {
					String result = fileDialog.open();
					if (result == null)
						break;
					if (!result.endsWith(EXT))
						result += EXT;
					try {
						FSDataStorage dataStorage = new FSDataStorage(new File(result), true);
						CorchyApplication.workspace().saveToStorage(dataStorage);						
						CorchyApplication.openWorkspaceWithStorage(dataStorage);
						break;
					} catch (Exception e) {
						MessageBox messageBox = new MessageBox(getShell(), SWT.OK | SWT.CANCEL);
						messageBox.setMessage("Impossible to save");
						messageBox.setText("Failed to save to file " + result + " due to:\n"
								+ e.getLocalizedMessage()
								+ "\nPlease select other file or click 'cancel'.");
						int open = messageBox.open();
						if (open == SWT.CANCEL)
							break;
					}
				}
			}
		});

		builder.separator();

		builder.item("Synchronize with remotes", new Runnable() {
			public void run() {
				performSync();
			}
		});

		return menu;
	}

	void createMenuBar(Shell shell) {
		Menu bar = new Menu(shell, SWT.BAR);
		shell.setMenuBar(bar);

		MenuItem fileItem = new MenuItem(bar, SWT.CASCADE);
		fileItem.setText("File");
		fileItem.setMenu(createFileMenu(shell));

		MenuItem editItem = new MenuItem(bar, SWT.CASCADE);
		editItem.setText("Edit");
		editItem.setMenu(createEditMenu(shell));
	}

	@Override
	protected void createControls(Composite parent) {
		createMenuBar(getShell());

		Composite editorBlock = new Composite(parent, SWT.NULL);
		createEditorAndOutline(editorBlock);
		editorBlock.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(
				true, true).create());
	}

	@Override
	protected void fillBottomBar(Composite bottomBar) {
		// Sync button
		Button syncButton = new Button(bottomBar, SWT.NONE | SWT.PUSH);
		((NSButton) syncButton.view).setBezelStyle(OS.NSTexturedRoundedBezelStyle);
		((NSButton) syncButton.view).setImagePosition(OS.NSImageOnly);
		syncButton.setImage(ICN_SYNC.get());
		syncButton.setLayoutData(GridDataFactory.defaultsFor(syncButton).align(SWT.BEGINNING,
				SWT.BEGINNING).indent(0, 0).create());
		syncButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				performSync();
			}
		});

		// Search field
		Text searchField = new Text(bottomBar, SWT.SINGLE | SWT.SEARCH);
		searchField.setText("Search");
		searchField.setLayoutData(GridDataFactory.defaultsFor(searchField).align(SWT.END,
				SWT.BEGINNING).indent(0, 0).create());
	}

	protected void performSync() {
		try {
			CorchyApplication.workspace().synchronize();
		} catch (StorageException e1) {
			// TODO
		}
	}

	@Override
	protected String dialogId() {
		return DIALOG_ID;
	}

	public void consume(WorkspaceSnapshot snapshot) {
		title = APP_TITLE + " - " + CorchyApplication.workspace().storage().getDescription();
		Shell shell = getShell();
		if (shell != null)
			shell.setText(title);
	}

}
