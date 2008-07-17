package com.mkalugin.corchy.internal.ui;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import com.google.common.collect.Iterables;
import com.mkalugin.pikachu.core.ast.ADocument;
import com.mkalugin.pikachu.core.ast.AProjectLine;
import com.mkalugin.pikachu.core.controllers.viewglue.OutlineView;
import com.mkalugin.pikachu.core.controllers.viewglue.OutlineViewCallback;
import com.mkalugin.swthell.CoolStyledTextScrollable;
import com.mkalugin.swthell.StyledTextEmbedder;

public class SwtCocoaOutlineView implements OutlineView {

	private final class OutlineContentProvider implements ITreeContentProvider {
		public Object[] getChildren(Object parentElement) {
			return new Object[0];
		}

		public Object getParent(Object element) {
			return null;
		}

		public boolean hasChildren(Object element) {
			return getChildren(element).length > 0;
		}

		public Object[] getElements(Object parentElement) {
			if (parentElement instanceof ADocument) {
				ADocument document = (ADocument) parentElement;
				Iterable<AProjectLine> projects = Iterables.filter(document.getChildren(), AProjectLine.class);
				return Iterables.newArray(projects, AProjectLine.class);
			}
			return  new Object[0];
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	private final class OutlineLabelProvider extends LabelProvider {	
		
		@Override
		public String getText(Object element) {
			if (element instanceof AProjectLine) {
				return ((AProjectLine) element).nameAsString();
			}
			return super.getText(element);
		}

	}

	private TreeViewer viewer;
    private OutlineViewCallback callback;
	private Color bgColor;
	private CoolStyledTextScrollable styledTextScrollable;
	private StyledTextEmbedder embedder;

	public SwtCocoaOutlineView(Composite parent) {
		createControl(parent);
	}

	private void createControl(Composite parent) {
		styledTextScrollable = new CoolStyledTextScrollable(parent, SWT.WRAP);
		styledTextScrollable.styledText().setAlignment(SWT.RIGHT);
		embedder = new StyledTextEmbedder(styledTextScrollable.styledText());
		
		Button button = new Button(styledTextScrollable.styledText(), 0);
		embedder.setTextWithControls("\uFFFC\n\uFFFC", new Control[] {button});
		
		
		
//		viewer = new TreeViewer(parent, SWT.V_SCROLL);
////		NSOutlineView tableView = (NSOutlineView) viewer.getControl().view;
////		tableView.setBackgroundColor(NSColor.colorWithDeviceRed((float) 209.0 / 255,
////				(float) 215.0 / 255, (float) 226.0 / 255, 1));
//		// TODO: fix setBackground in SWT/Cocoa
//		bgColor = new Color(Display.getDefault(), 209, 215, 226);
//		viewer.getTree().setBackground(bgColor);
//		viewer.setContentProvider(new OutlineContentProvider());
//		viewer.setLabelProvider(new OutlineLabelProvider());
//		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
//
//			public void selectionChanged(SelectionChangedEvent event) {
//				ISelection selection = event.getSelection();
//				if (selection instanceof IStructuredSelection) {
//					IStructuredSelection s = (IStructuredSelection) selection;
//					if (!s.isEmpty()) {
//					    AProjectLine element = (AProjectLine) s.getFirstElement();
//					    callback.projectSelected(element.name());					
//					}
//				}
//			}
//			
//		});
//		viewer.getTree().addDisposeListener(new DisposeListener() {
//
//			public void widgetDisposed(DisposeEvent e) {
//				bgColor.dispose();
//			}
//			
//		});
	}

	public void setLayoutData(Object outlineData) {
//		viewer.getControl().setLayoutData(outlineData);
		styledTextScrollable.setLayoutData(outlineData);
	}

    public OutlineView bind(OutlineViewCallback callback) {
        if (callback == null)
            throw new NullPointerException("callback is null");
        if (this.callback != null)
            throw new IllegalStateException("callback is already set");
        this.callback = callback;
        return this;
    }

	public void setDocument(final ADocument documentNode) {
		Display.getDefault().syncExec(new Runnable() {

			public void run() {
				//viewer.setInput(documentNode);
			}
			
		});
	}

}
