/**
 * 
 */
package com.mkalugin.corchy.internal.ui.editor;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;

import com.mkalugin.pikachu.core.controllers.search.SearchMatch;
import com.mkalugin.pikachu.core.controllers.search.SearchResult;
import com.mkalugin.swthell.CoolStyledTextScrollable;

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
            
            int clientAreaHeight = styledText.getClientArea().height;
            for (int i = 0; i < count; i++) {
                gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
                if (i == activeMatchNumber) {
                    gc.setAlpha(200);
                } else {
                    gc.setAlpha(100);
                }
                SearchMatch match = searchResult.getMatchWithNumber(i);
                Rectangle bounds = styledText.getTextBounds(match.startOffset(), match.endOffset());
                // manual clipping
                if (bounds.y + bounds.height < 0)
                    continue;
                if (bounds.y > clientAreaHeight)
                    continue;
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
    private CoolStyledTextScrollable styledTextScrollable;
    private Point focusRange;
    
    CorchyViewer(Composite parent) {
        super(parent, null, SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
        configure(new CorchySourceViewerConfiguration());
    }
    
    @Override
    public Control getControl() {
        return styledTextScrollable;
    }
    
    @Override
    protected StyledText createTextWidget(Composite parent, int styles) {
        styledText = super.createTextWidget(parent, styles ^ SWT.V_SCROLL);
        styledTextScrollable = new CoolStyledTextScrollable(parent, styledText);
        ((GridLayout) (styledTextScrollable.getLayout())).marginLeft = 20;
        styledText.setIndent(15);
        styledText.setLineSpacing(4);
        
        styledTextScrollable.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL,
                SWT.FILL).create());
        
        styledText.setData(VIEWER_KEY, this);
        searchResultHighlighter = new SearchResultHighlighter();
        styledText.addPaintListener(searchResultHighlighter);
        styledText.addPaintListener(new PaintListener() {
            
            public void paintControl(PaintEvent e) {
                if (focusRange != null) {
                    Rectangle clientArea = styledText.getClientArea();
                    Rectangle bounds = styledText.getTextBounds(focusRange.x, focusRange.y);
                    int y1 = bounds.y;
                    int y2 = bounds.y + bounds.height;
                    e.gc.setAlpha(100);
                    e.gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
                    e.gc.fillRectangle(0, 0, clientArea.width, y1);
                    e.gc.fillRectangle(0, y2, clientArea.width, clientArea.height - y2);
                }
            }
            
        });
        styledTextScrollable.layout();
        return styledText;
    }
    
    @Override
    protected Layout createLayout() {
        return GridLayoutFactory.fillDefaults().extendedMargins(0, 0, 0, 0).create();
    }
    
    public void highlightSearchResults(SearchResult result) {
        if (result == null)
            throw new IllegalArgumentException("result is null");
        searchResult = result;
        activeMatchNumber = 0;
        styledText.redraw();
    }
    
    public void highlightMatch(int number) {
        activeMatchNumber = number;
        styledText.redraw();
    }
    
    public void setSelectionTo(int start, int end) {
        styledText.setSelection(start, end);
        styledTextScrollable.updateScrollbarPosition();
    }
    
    public void setFocusRange(int start, int end) {
        this.focusRange = new Point(start, end);
        styledText.getParent().setBackground(new Color(Display.getDefault(), 155, 155, 155));
    }
    
    public void resetFocusRange() {
        this.focusRange = null;
        styledText.getParent().setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
    }
    
}