package com.mkalugin.pikachu.core.controllers;

import static com.yoursway.utils.Listeners.newListenersByIdentity;

import com.mkalugin.pikachu.core.model.document.Element;
import com.mkalugin.pikachu.core.model.document.Named;
import com.mkalugin.pikachu.core.model.document.Range;
import com.yoursway.utils.Listeners;

public class Selection {
    
    private Element selectedElement;
    
    public Selection() {
    }
    
    private transient final Listeners<SelectionListener> listeners = newListenersByIdentity();
    
    public synchronized void addListener(SelectionListener listener) {
        listeners.add(listener);
    }
    
    public synchronized void removeListener(SelectionListener listener) {
        listeners.remove(listener);
    }
    
    public void setTo(Element element, Object sender) {
        selectedElement = element;
        for (SelectionListener listener : listeners)
            listener.selectionChanged(sender);
    }
    
    public Element selectedElement() {
        return selectedElement;
    }
    
    public Range selectionRange() {
        Element el;
        
        if (selectedElement instanceof Named)
            el = ((Named) selectedElement).getNameToken();
        else
            el = selectedElement;
        
        return el.range();
    }
    
}
