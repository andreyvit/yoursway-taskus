package com.mkalugin.pikachu.core.model.document;

import java.util.List;

public interface Container {
    
    public void addChild(Element child);
    
    public void removeChild(Element child);
    
    public List<Element> getChildren();
    
}