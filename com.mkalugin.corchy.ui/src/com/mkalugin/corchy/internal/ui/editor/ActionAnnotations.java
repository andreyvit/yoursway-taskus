package com.mkalugin.corchy.internal.ui.editor;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;

public class ActionAnnotations {

	private class AnnotationBinding {
		IActionAnnotation annotation;
		int bindingOffset;

		public AnnotationBinding(IActionAnnotation annotation, int bindingOffset) {
			this.annotation = annotation;
			this.bindingOffset = bindingOffset;
		}
	}

	private class AnnotationsPainter implements PaintListener {

		private static final int SPACING = 10;

		public void paintControl(PaintEvent e) {
			synchronized (annotations) {
				ArrayListMultimap<Integer, IActionAnnotation> yToAnnotations = Multimaps
						.newArrayListMultimap();
				Rectangle clientArea = styledText.getClientArea();
				for (AnnotationBinding b : annotations) {
					Rectangle bounds;
					try {
						bounds = styledText.getTextBounds(b.bindingOffset,
								b.bindingOffset);
					} catch (IllegalArgumentException e1) {
						continue;
					}
					if (bounds.y >= 0 && bounds.y < clientArea.height) {
						yToAnnotations.get(bounds.y).add(b.annotation);
					}
				}
				lastPaintRects.clear();
				for (Entry<Integer, Collection<IActionAnnotation>> row : yToAnnotations.asMap()
						.entrySet()) {
					int y = row.getKey();
					Collection<IActionAnnotation> annotations = row.getValue();
					int x = clientArea.width;
					for (IActionAnnotation a : annotations)
						x -= a.computeSize().x + SPACING;
					for (IActionAnnotation a : annotations) {
						a.render(e.gc, new Point(x, y));
						Point computedSize = a.computeSize();
						lastPaintRects.put(a, new Rectangle(x, y, computedSize.x, computedSize.y));
						x += a.computeSize().x + SPACING;
					}
				}
			}
		}
	}

	private class AnnotationsMouseWizard implements MouseListener, MouseTrackListener,
			MouseMoveListener, MouseWheelListener {

		IActionAnnotation activeAnnotation;

		public void mouseDoubleClick(MouseEvent e) {
			if (activeAnnotation != null)
				activeAnnotation.mouseDoubleClick(e);
		}

		public void mouseDown(MouseEvent e) {
			if (activeAnnotation != null)
				activeAnnotation.mouseDown(e);
		}

		public void mouseUp(MouseEvent e) {
			if (activeAnnotation != null) {
				activeAnnotation.mouseUp(e);
				activeAnnotation.doAction();
			}
		}

		private IActionAnnotation annotationAtPoint(Point point) {
			for (Entry<IActionAnnotation, Rectangle> e : lastPaintRects.entrySet()) {
				if (e.getValue().contains(point))
					return e.getKey();
			}
			return null;
		}

		public void mouseEnter(MouseEvent e) {
			updateActiveAnnotation(e);
		}

		public void mouseExit(MouseEvent e) {
			if (activeAnnotation != null)
				activeAnnotation.mouseExit(e);
			activeAnnotation = null;
		}

		public void mouseHover(MouseEvent e) {
			updateActiveAnnotation(e);
			if (activeAnnotation != null)
				activeAnnotation.mouseHover(e);
		}

		public void mouseMove(MouseEvent e) {
			updateActiveAnnotation(e);
		}

		public void mouseScrolled(MouseEvent e) {
			updateActiveAnnotation(e);
		}

		private void updateActiveAnnotation(MouseEvent e) {
			IActionAnnotation annotationAtPoint = annotationAtPoint(new Point(e.x, e.y));
			if (annotationAtPoint != activeAnnotation) {
				if (activeAnnotation != null)
					activeAnnotation.mouseExit(e);
				if (annotationAtPoint != null)
					annotationAtPoint.mouseEnter(e);
				activeAnnotation = annotationAtPoint;
			}
		}

	}

	private final StyledText styledText;
	private List<AnnotationBinding> annotations = newArrayList();
	private AnnotationsMouseWizard annotationsMouseWizard;
	private AnnotationsPainter annotationsPainter;
	private Map<IActionAnnotation, Rectangle> lastPaintRects = Maps.newHashMap();

	public ActionAnnotations(StyledText styledText) {
		if (styledText == null)
			throw new IllegalArgumentException("styledText is null");
		this.styledText = styledText;
		annotationsMouseWizard = new AnnotationsMouseWizard();
		styledText.addMouseListener(annotationsMouseWizard);
		styledText.addMouseTrackListener(annotationsMouseWizard);
		styledText.addMouseMoveListener(annotationsMouseWizard);
		styledText.addMouseWheelListener(annotationsMouseWizard);
		annotationsPainter = new AnnotationsPainter();
		styledText.addPaintListener(annotationsPainter);
	}

	public void resetAnnotations() {
		synchronized (annotations) {
			lastPaintRects.clear();
			for (AnnotationBinding ann : annotations) {
				ann.annotation.dispose();
			}
			annotations.clear();
			
		}
	}

	public void addAnnotation(IActionAnnotation annotation, int bindingOffset) {
		if (annotation == null)
			throw new IllegalArgumentException("annotation is null");
		if (bindingOffset < 0 || styledText.getCharCount() <= bindingOffset)
			throw new IllegalArgumentException("bindingOffset is out of bounds");
		synchronized (annotations) {
			annotations.add(new AnnotationBinding(annotation, bindingOffset));
		}
	}

}
