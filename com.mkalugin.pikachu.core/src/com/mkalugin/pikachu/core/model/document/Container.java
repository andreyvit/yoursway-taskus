package com.mkalugin.pikachu.core.model.document;

import java.util.List;

public interface Container extends Element {
    
    public void addChild(Element child);
    
    public void removeChild(Element child);
    
    public List<Element> getChildren();
    
    public boolean doesChildMatch(Element child);
    
}