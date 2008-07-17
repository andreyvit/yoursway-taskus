package com.mkalugin.corchy.internal.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import com.google.common.collect.Iterables;
import com.mkalugin.pikachu.core.ast.ADocument;
import com.mkalugin.pikachu.core.ast.ADocumentLevelNode;
import com.mkalugin.pikachu.core.ast.AProjectLine;
import com.mkalugin.pikachu.core.ast.AProjectName;
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
				Iterable<AProjectLine> projects = Iterables.filter(document.getChildren(),
						AProjectLine.class);
				return Iterables.newArray(projects, AProjectLine.class);
			}
			return new Object[0];
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

	private OutlineViewCallback callback;
	private CoolStyledTextScrollable styledTextScrollable;
	private StyledTextEmbedder embedder;
	private Font font;

	private List<Control> controls = new ArrayList<Control>();

	public SwtCocoaOutlineView(Composite parent) {
		createControl(parent);
	}

	private void createControl(Composite parent) {
		styledTextScrollable = new CoolStyledTextScrollable(parent, SWT.WRAP);
		styledTextScrollable.styledText().setAlignment(SWT.RIGHT);
		styledTextScrollable.styledText().setEditable(false);
		font = new Font(Display.getDefault(), "Gill Sans", 16, SWT.BOLD);
		styledTextScrollable.styledText().setFont(font);
		embedder = new StyledTextEmbedder(styledTextScrollable.styledText());

		// OutlineItem item1 = new
		// OutlineItem(styledTextScrollable.styledText());
		// item1.setText("Foo");
		// OutlineItem item2 = new
		// OutlineItem(styledTextScrollable.styledText());
		// item2.setText("Bar");
		// item2.setActive(true);
		//		
		// embedder.setTextWithControls("Projects:\n\n\uFFFC\n\uFFFC", new
		// Control[] {item1, item2});

		styledTextScrollable.addDisposeListener(new DisposeListener() {

			public void widgetDisposed(DisposeEvent e) {
				font.dispose();
			}

		});

		// viewer = new TreeViewer(parent, SWT.V_SCROLL);
		// // NSOutlineView tableView = (NSOutlineView)
		// viewer.getControl().view;
		// // tableView.setBackgroundColor(NSColor.colorWithDeviceRed((float)
		// 209.0 / 255,
		// // (float) 215.0 / 255, (float) 226.0 / 255, 1));
		// // TODO: fix setBackground in SWT/Cocoa
		// bgColor = new Color(Display.getDefault(), 209, 215, 226);
		// viewer.getTree().setBackground(bgColor);
		// viewer.setContentProvider(new OutlineContentProvider());
		// viewer.setLabelProvider(new OutlineLabelProvider());
		// viewer.addSelectionChangedListener(new ISelectionChangedListener() {
		//
		// public void selectionChanged(SelectionChangedEvent event) {
		// ISelection selection = event.getSelection();
		// if (selection instanceof IStructuredSelection) {
		// IStructuredSelection s = (IStructuredSelection) selection;
		// if (!s.isEmpty()) {
		// AProjectLine element = (AProjectLine) s.getFirstElement();
		// callback.projectSelected(element.name());
		// }
		// }
		// }
		//			
		// });
		// viewer.getTree().addDisposeListener(new DisposeListener() {
		//
		// public void widgetDisposed(DisposeEvent e) {
		// bgColor.dispose();
		// }
		//			
		// });
	}

	public void setLayoutData(Object outlineData) {
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
		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				destroyControls();
				List<ADocumentLevelNode> nodes = documentNode.getChildren();
				for (ADocumentLevelNode n : nodes) {
					if (n instanceof AProjectLine) {
						final AProjectLine projectLine = (AProjectLine) n;
						OutlineItem item = new OutlineItem(styledTextScrollable.styledText());
						item.setText(projectLine.nameAsString());
						item.setData(projectLine.name());
						item.addMouseListener(new MouseListener() {

							public void mouseDoubleClick(MouseEvent e) {
							}

							public void mouseDown(MouseEvent e) {
							}

							public void mouseUp(MouseEvent e) {
								AProjectName name = (AProjectName) e.widget.getData();
								callback.projectSelected(name);
							}
							
						});
						controls.add(item);
					}
				}
				String text = "Projects:\n\n";
				if (controls.size() == 0)
					text += "(none)";
				else {
					for (int i = 0; i < controls.size(); i++)
						text += "\uFFFC\n";
				}
				embedder.setTextWithControls(text, controls.toArray(new Control[controls.size()]));
			}

		});
	}

	protected void destroyControls() {
		for (Control c : controls)
			c.dispose();
		controls.clear();
	};

}
