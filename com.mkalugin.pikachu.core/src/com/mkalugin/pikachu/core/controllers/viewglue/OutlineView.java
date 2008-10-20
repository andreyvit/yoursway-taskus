package com.mkalugin.pikachu.core.controllers.viewglue;

import com.mkalugin.pikachu.core.model.document.Element;
import com.mkalugin.pikachu.core.model.document.TaggedContainer;

public interface OutlineView {
    
    void setDocument(TaggedContainer contentModel);
    
    void setActiveItem(Element selectedElement);
    
}
