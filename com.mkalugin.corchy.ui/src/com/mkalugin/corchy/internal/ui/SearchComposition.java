package com.mkalugin.corchy.internal.ui;

import static com.mkalugin.corchy.ui.controls.PlatformStuff.setPlaceholderString;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.mkalugin.pikachu.core.controllers.search.SearchCallback;

public class SearchComposition {

	private Composite searchNavigationComposite;
	// private Label matchesCountLabel;
	private Text searchField;
	private SearchCallback searchCallback;
	private Composite composite;
	private MatchesCountLabel matchesCountLabel2;

	public SearchComposition(Composite parent) {
		createControls(parent);
	}

	public void setLayoutData(Object data) {
		composite.setLayoutData(data);
	}

	private void createControls(Composite parent) {
		composite = new Composite(parent, SWT.TRANSPARENT);

		searchNavigationComposite = new Composite(composite, SWT.TRANSPARENT);
		searchNavigationComposite.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL,
				SWT.BEGINNING).grab(true, false).indent(0, -2).create());
		createSearchNavigationControls(searchNavigationComposite);
		searchNavigationComposite.setVisible(false);

		searchField = new Text(composite, SWT.SINGLE | SWT.SEARCH);
		setPlaceholderString(searchField, "Search");
		searchField.setLayoutData(GridDataFactory.defaultsFor(searchField).align(SWT.END,
				SWT.BEGINNING).grab(false, false).indent(4, -2).create());
		searchField.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				searchCallback.setSearchPattern(searchField.getText());
			}

		});
		searchField.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				switch (e.character) {
				case SWT.ESC:
					searchCallback.escPressed();
					e.doit = false;
					break;
				case SWT.CR:
					searchCallback.returnPressed();
					e.doit = false;
					break;
				}
			}

			public void keyReleased(KeyEvent e) {
			}

		});

		GridLayoutFactory.swtDefaults().numColumns(2).margins(0, 0).spacing(8, 0).generateLayout(
				composite);
	}

	private class MatchesCountLabel extends Canvas {

		private String text;

		public MatchesCountLabel(Composite parent, int style) {
			super(parent, style);
			this.addPaintListener(new PaintListener() {

				public void paintControl(PaintEvent e) {
					Point textExtent = e.gc.textExtent(text);
					e.gc.drawText(text, e.width - textExtent.x, 0, true);
				}

			});
		}

		public void setText(String text) {
			this.text = text;
			this.redraw();
		}

	}

	private void createSearchNavigationControls(Composite composite) {
		matchesCountLabel2 = new MatchesCountLabel(composite, SWT.TRANSPARENT);
		matchesCountLabel2.setLayoutData(GridDataFactory.swtDefaults()
				.align(SWT.END, SWT.BEGINNING).indent(0, 5).grab(true, false)
				.hint(100, SWT.DEFAULT).minSize(100, SWT.DEFAULT).create());

		Button prevButton = new Button(composite, SWT.ARROW | SWT.LEFT);
		// prevButton.setImage(IMG_LEFT_ARROW.get());
		prevButton.setLayoutData(GridDataFactory.defaultsFor(prevButton).align(SWT.END,
				SWT.BEGINNING).indent(0, 7).hint(32, SWT.DEFAULT).create());
		prevButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				searchCallback.previousMatch();
			}
		});

		Button nextButton = new Button(composite, SWT.ARROW | SWT.RIGHT);
		// nextButton.setImage(IMG_RIGHT_ARROW.get());
		nextButton.setLayoutData(GridDataFactory.defaultsFor(nextButton).align(SWT.END,
				SWT.BEGINNING).indent(0, 7).hint(32, SWT.DEFAULT).create());
		nextButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				searchCallback.nextMatch();
			}
		});

		GridLayoutFactory.fillDefaults().numColumns(3).extendedMargins(0, 0, 0, 0).margins(0, 0)
				.spacing(8, 0).generateLayout(composite);
	}

	public void setCallback(SearchCallback callback) {
		searchCallback = callback;
	}

	public void setNavigationVisible(boolean visible) {
		searchNavigationComposite.setVisible(visible);
	}

	public void setMatchesCount(int matchesCount) {
		if (matchesCount != 1)
			matchesCountLabel2.setText(matchesCount + " matches");
		else
			matchesCountLabel2.setText(matchesCount + " match");		
	}

	public void setText(String string) {
		searchField.setText(string);
	}

	public void setFocus() {
		searchField.setFocus();
	}

	public boolean navigationEnabled() {
		return searchNavigationComposite.isVisible();
	}

}
