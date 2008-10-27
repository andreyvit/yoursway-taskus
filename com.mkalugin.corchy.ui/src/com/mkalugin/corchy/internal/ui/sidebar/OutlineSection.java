package com.mkalugin.corchy.internal.ui.sidebar;

import java.util.List;

import com.mkalugin.pikachu.core.model.document.TaggedContainer;
import com.yoursway.swt.coolsidebar.viewmodel.SidebarItem;
import com.yoursway.swt.coolsidebar.viewmodel.SidebarSection;

public class OutlineSection implements SidebarSection {
    
    private final TaggedContainer contentModel;
    
    public OutlineSection(TaggedContainer contentModel) {
        if (contentModel == null)
            throw new NullPointerException("contentModel is null");
        
        this.contentModel = contentModel;
    }
    
    public List<SidebarItem> children() {
        return OutlineItem.itemsFrom(contentModel.getChildren());
    }
    
    public boolean collapsable() {
        return false;
    }
    
    public String name() {
        return "Outline";
    }
    
}
