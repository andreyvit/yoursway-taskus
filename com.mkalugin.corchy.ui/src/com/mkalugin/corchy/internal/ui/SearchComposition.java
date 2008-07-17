package com.mkalugin.corchy.internal.ui;

import static com.mkalugin.corchy.internal.ui.images.CorchyImages.IMG_LEFT_ARROW;
import static com.mkalugin.corchy.internal.ui.images.CorchyImages.IMG_RIGHT_ARROW;
import static com.mkalugin.corchy.ui.controls.PlatformStuff.setPlaceholderString;
import static com.mkalugin.corchy.ui.controls.PlatformStuff.texturedButton;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.mkalugin.pikachu.core.controllers.search.SearchCallback;

public class SearchComposition {

	private Composite searchNavigationComposite;
	private Label matchesCountLabel;
	private Text searchField;
	private SearchCallback searchCallback;
	private Composite composite;

	public SearchComposition(Composite parent) {
		createControls(parent);
	}

	public void setLayoutData(Object data) {
		composite.setLayoutData(data);
	}
	
	private void createControls(Composite parent) {
		composite = new Composite(parent, SWT.NONE);
		
		searchNavigationComposite = new Composite(composite, SWT.NONE);
        searchNavigationComposite.setLayoutData(GridDataFactory.swtDefaults().
        		align(SWT.FILL, SWT.BEGINNING).grab(true, false).indent(0, 0).create());
        createSearchNavigationControls(searchNavigationComposite);
        searchNavigationComposite.setVisible(false);
        
        searchField = new Text(composite, SWT.SINGLE | SWT.SEARCH);
        setPlaceholderString(searchField, "Search");
        searchField.setLayoutData(GridDataFactory.defaultsFor(searchField).
        		align(SWT.END, SWT.BEGINNING).grab(false, false)
                .indent(8, 0).create());
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
        
        GridLayoutFactory.swtDefaults().numColumns(2).extendedMargins(0, 0, 0, 0).margins(0, 0)
        	.spacing(8, 0).generateLayout(composite);
	}
	
	private void createSearchNavigationControls(Composite composite) {
    	matchesCountLabel = new Label(composite, SWT.RIGHT);
    	matchesCountLabel.setLayoutData(GridDataFactory.swtDefaults().align(SWT.END, SWT.BEGINNING)
                .indent(0, 5).grab(true, false).minSize(100, SWT.DEFAULT).create());
    	matchesCountLabel.setText("stub");    	
    	
    	Button prevButton = texturedButton(composite);    	
    	prevButton.setImage(IMG_LEFT_ARROW.get());
    	prevButton.setLayoutData(GridDataFactory.defaultsFor(prevButton).align(SWT.END, SWT.BEGINNING)
                .indent(0, 0).create());
    	prevButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {            	
                searchCallback.previousMatch();
            }
        });
    	
    	Button nextButton = texturedButton(composite);    	
    	nextButton.setImage(IMG_RIGHT_ARROW.get());
    	nextButton.setLayoutData(GridDataFactory.defaultsFor(nextButton).align(SWT.END, SWT.BEGINNING)
                .indent(0, 0).create());
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
			matchesCountLabel.setText(matchesCount + " matches");
		else
			matchesCountLabel.setText(matchesCount + " match");
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
