/**
 * 
 */
package com.mkalugin.corchy.internal.editor;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;

import com.mkalugin.pikachu.core.controllers.search.SearchMatch;
import com.mkalugin.pikachu.core.controllers.search.SearchResult;

public class CorchyViewer extends SourceViewer {

	private SearchResult searchResult;
	private int activeMatchNumber;

	private final class SearchResultHighlighter implements PaintListener {

		public void paintControl(PaintEvent e) {
			if (searchResult == null)
				return;
			hightlightResult(e.gc, searchResult, activeMatchNumber);
		}

		private void hightlightResult(GC gc, SearchResult searchResult, int activeMatchNumber) {
			int count = searchResult.matchesCount();
			if (count == 0)
				return;

//			gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
//			gc.setAlpha(10);
//			gc.fillRectangle(styledText.getClientArea());

			for (int i = 0; i < count; i++) {
				gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
				if (i == activeMatchNumber) {
					gc.setAlpha(200);
				} else {
					gc.setAlpha(150);
				}
				SearchMatch match = searchResult.getMatchWithNumber(i);
				Rectangle bounds = styledText.getTextBounds(match.startOffset(), match
						.endOffset());
				gc.setLineWidth(2);
				gc.drawRoundRectangle(bounds.x - 1, bounds.y - 1, bounds.width + 2, bounds.height + 2, 4, 4);
				styledText.redrawRange(match.startOffset(), match.endOffset() - match.startOffset(), true);
			}			
		}

	}

	private static final String VIEWER_KEY = "textViewer";

	public final static CorchyViewer fromControl(StyledText control) {
		Object data = control.getData(VIEWER_KEY);
		if (data instanceof CorchyViewer)
			return (CorchyViewer) data;
		return null;
	}

	private StyledText styledText;
	private SearchResultHighlighter searchResultHighlighter;

	CorchyViewer(Composite parent) {
		super(parent, null, SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
		configure(new CorchySourceViewerConfiguration());
	}

	@Override
	protected StyledText createTextWidget(Composite parent, int styles) {
		styledText = super.createTextWidget(parent, styles);
		styledText.setIndent(15);
		styledText.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL,
				SWT.FILL).create());
		styledText.setData(VIEWER_KEY, this);
		searchResultHighlighter = new SearchResultHighlighter();
		styledText.addPaintListener(searchResultHighlighter);
		return styledText;
	}

	@Override
	protected Layout createLayout() {
		return GridLayoutFactory.fillDefaults().extendedMargins(0, 0, 0, 0).create();
	}

	public void highlightSearchResults(SearchResult result) {
		searchResult = result;
		if (searchResult != null && searchResult.matchesCount() > 0)
			highlightMatch(0);
		else {			
			styledText.setSelection(0);
			styledText.redraw();
		}
	}

	public void highlightMatch(int number) {
		activeMatchNumber = number;
		SearchMatch match = searchResult.getMatchWithNumber(number);
		int startOffset = match.startOffset();
		int endOffset = match.endOffset() + 1;
		styledText.setSelection(startOffset, endOffset);
		styledText.redraw();
	}

}