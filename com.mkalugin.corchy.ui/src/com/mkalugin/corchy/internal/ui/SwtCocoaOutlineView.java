package com.mkalugin.corchy.internal.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.ietf.jgss.Oid;

import com.mkalugin.pikachu.core.ast.ADocument;
import com.mkalugin.pikachu.core.ast.ADocumentLevelNode;
import com.mkalugin.pikachu.core.ast.AProjectLine;
import com.mkalugin.pikachu.core.ast.AProjectName;
import com.mkalugin.pikachu.core.controllers.viewglue.OutlineView;
import com.mkalugin.pikachu.core.controllers.viewglue.OutlineViewCallback;
import com.mkalugin.swthell.CoolStyledTextScrollable;
import com.mkalugin.swthell.StyledTextEmbedder;

public class SwtCocoaOutlineView implements OutlineView {

	private OutlineViewCallback callback;
	private CoolStyledTextScrollable styledTextScrollable;
	private StyledTextEmbedder embedder;
	private Font font;

	private List<OutlineItem> controls = new ArrayList<OutlineItem>();

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

		styledTextScrollable.addDisposeListener(new DisposeListener() {

			public void widgetDisposed(DisposeEvent e) {
				font.dispose();
			}

		});
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
		for (OutlineItem c : controls)
			c.dispose();
		controls.clear();
	}

	public void setActiveProject(final AProjectName selectedProject) {
		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				for (OutlineItem item : controls) {
					AProjectName name = (AProjectName) item.getData();
					item.setActive(name.equals(selectedProject));
				}
				styledTextScrollable.redraw();
			}
		});
	};

}
