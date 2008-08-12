package com.mkalugin.corchy.internal.ui;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.google.common.collect.Lists;
import com.mkalugin.swthell.CoolScrolledComposite;

public class CoolOutlineView {

	private List<OutlineItem> items= Lists.newArrayList();
	private ILabelProvider labelProvider;
	private CoolScrolledComposite scrolledComposite;
	private Canvas canvas;
	private OutlineItem titleItem;
	private List<MouseListener> mouseListeners = Lists.newArrayList();

	public CoolOutlineView(Composite parent) {
		scrolledComposite = new CoolScrolledComposite(parent, SWT.NONE);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		canvas = new Canvas(scrolledComposite.parentComposite(), SWT.NONE);
		canvas.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		GridLayoutFactory.fillDefaults().generateLayout(canvas);
		scrolledComposite.setContent(canvas);
		titleItem = new OutlineItem(canvas, false, true);
		titleItem.setText("Title");
		titleItem.setLayoutData(GridDataFactory.fillDefaults().align(SWT.END, SWT.BEGINNING).grab(true, false).create());
		labelProvider = new LabelProvider();		
	}
	

	public void addItemsMouseListener(MouseListener listener) {
		mouseListeners.add(listener);
	}
	

	public void setTitle(String title) {
		titleItem.setText(title);
	}

	public void setElements(List<Object> elements) {
		int count = this.items.size();
		int newCount = elements.size();
		if (count < elements.size()) {
			for (int i = 0; i < newCount - count; i++)
				this.items.add(newItem());
		}
		if (count > newCount) {
			for (int i = 0; i < count - newCount; i++) {
				OutlineItem item = items.get(0);
				item.dispose();
				items.remove(0);
			}
		}
		Iterator<Object> elemIter = elements.iterator();
		Iterator<OutlineItem> itemsIter = items.iterator();
		while (elemIter.hasNext()) {
			OutlineItem outlineItem = itemsIter.next();
			Object element = elemIter.next();
			outlineItem.setData(element);
			outlineItem.setText(labelProvider.getText(element));
		}
		scrolledComposite.setMinSize(canvas.computeSize(SWT.DEFAULT, SWT.DEFAULT));	
		canvas.layout(true);
		canvas.redraw();
	}


	private OutlineItem newItem() {
		OutlineItem outlineItem = new OutlineItem(canvas);
		outlineItem.addMouseListener(new MouseListener() {

			public void mouseDoubleClick(MouseEvent e) {
				for (MouseListener l : mouseListeners) {
					l.mouseDoubleClick(e);
				}
			}

			public void mouseDown(MouseEvent e) {
				for (MouseListener l : mouseListeners) {
					l.mouseDown(e);
				}
			}

			public void mouseUp(MouseEvent e) {
				for (MouseListener l : mouseListeners) {
					l.mouseUp(e);
				}
			}
			
		});
		outlineItem.setLayoutData(GridDataFactory.fillDefaults().align(SWT.END, SWT.BEGINNING).grab(true, false).create());
		return outlineItem;
	}

	public void setLabelProvider(ILabelProvider provider) {
		if (provider == null)
			throw new NullPointerException("provider is null");
		this.labelProvider = provider;
	}



	public void setLayoutData(Object layoutData) {
		scrolledComposite.setLayoutData(layoutData);
	}


	public List<OutlineItem> items() {
		return items;
	}


	public void redraw() {
		scrolledComposite.redraw();
	}

}
