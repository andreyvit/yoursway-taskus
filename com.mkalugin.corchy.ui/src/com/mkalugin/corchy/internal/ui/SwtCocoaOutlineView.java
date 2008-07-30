package com.mkalugin.corchy.internal.ui;

import java.util.List;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mkalugin.pikachu.core.ast.ADocument;
import com.mkalugin.pikachu.core.ast.AProjectName;
import com.mkalugin.pikachu.core.controllers.viewglue.OutlineView;
import com.mkalugin.pikachu.core.controllers.viewglue.OutlineViewCallback;
import com.mkalugin.pikachu.core.model.document.structure.MDocument;
import com.mkalugin.pikachu.core.model.document.structure.MElement;
import com.mkalugin.pikachu.core.model.document.structure.MProject;
import com.mkalugin.pikachu.core.model.document.structure.builder.StructuredModelBuilder;

public class SwtCocoaOutlineView implements OutlineView {

	private OutlineViewCallback callback;

	private CoolOutlineView coolOutlineView;

	public SwtCocoaOutlineView(Composite parent) {
		createControl(parent);
	}

	private void createControl(Composite parent) {
		coolOutlineView = new CoolOutlineView(parent);
		coolOutlineView.setTitle("Projects:");
		coolOutlineView.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof MProject) {
					return ((MProject) element).getName();
				}
				return super.getText(element);
			}
		});
		coolOutlineView.addItemsMouseListener(new MouseListener() {

			public void mouseDoubleClick(MouseEvent e) {
			}

			public void mouseDown(MouseEvent e) {
			}

			public void mouseUp(MouseEvent e) {
				MProject project = (MProject) e.widget.getData();
				AProjectName name = project.getLine().name();
				callback.projectSelected(name);
				SwtCocoaOutlineView.this.setActiveProject(name);
			}
			
		});
	}


	
	public void setLayoutData(Object outlineData) {
		coolOutlineView.setLayoutData(outlineData);
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
				StructuredModelBuilder modelBuilder = new StructuredModelBuilder();
				MDocument document = modelBuilder.buildStructure(documentNode);
				List<MElement> children = document.getChildren();
				List<?> projects = Lists.newArrayList(Iterables.filter(children, MProject.class));
				coolOutlineView.setElements((List<Object>) projects);
				coolOutlineView.redraw();
			}

		});
	}

	public void setActiveProject(final AProjectName selectedProject) {
		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				for (OutlineItem item : coolOutlineView.items()) {
					MProject project = (MProject) item.getData();
					AProjectName name = project.getLine().name();
					item.setActive(name.equals(selectedProject));
				}
				coolOutlineView.redraw();
			}
		});
	};

}
