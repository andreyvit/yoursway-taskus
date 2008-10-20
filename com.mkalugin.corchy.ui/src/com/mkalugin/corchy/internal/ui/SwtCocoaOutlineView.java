package com.mkalugin.corchy.internal.ui;

import java.util.List;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.mkalugin.pikachu.core.controllers.viewglue.OutlineView;
import com.mkalugin.pikachu.core.controllers.viewglue.OutlineViewCallback;
import com.mkalugin.pikachu.core.model.document.Element;
import com.mkalugin.pikachu.core.model.document.Named;
import com.mkalugin.pikachu.core.model.document.TaggedContainer;

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
                if (element instanceof Named)
                    return ((Named) element).getName();
                
                return super.getText(element);
            }
        });
        coolOutlineView.addItemsMouseListener(new MouseListener() {
            
            public void mouseDoubleClick(MouseEvent e) {
            }
            
            public void mouseDown(MouseEvent e) {
            }
            
            public void mouseUp(MouseEvent e) {
                Element element = (Element) e.widget.getData();
                callback.elementSelected(element);
                SwtCocoaOutlineView.this.setActiveItem(element);
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
    
    public void setDocument(final TaggedContainer contentModel) {
        Display.getDefault().asyncExec(new Runnable() {
            
            public void run() {
                List<Element> children = contentModel.getChildren();
                coolOutlineView.setElements(children);
                coolOutlineView.redraw();
            }
            
        });
    }
    
    public void setActiveItem(final Element selectedElement) {
        Display.getDefault().asyncExec(new Runnable() {
            
            public void run() {
                for (OutlineItem item : coolOutlineView.items()) {
                    Element element = (Element) item.getData();
                    item.setActive(element.equals(selectedElement));
                }
                coolOutlineView.redraw();
            }
            
        });
    };
    
}
