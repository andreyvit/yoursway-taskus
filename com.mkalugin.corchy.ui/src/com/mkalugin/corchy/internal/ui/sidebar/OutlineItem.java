package com.mkalugin.corchy.internal.ui.sidebar;

import static com.google.common.collect.Lists.newLinkedList;

import java.util.List;

import com.mkalugin.pikachu.core.model.document.Element;
import com.mkalugin.pikachu.core.model.document.NamedContainer;
import com.mkalugin.pikachu.core.model.document.Task;
import com.yoursway.swt.coolsidebar.viewmodel.SidebarIcon;
import com.yoursway.swt.coolsidebar.viewmodel.SidebarItem;

public class OutlineItem implements SidebarItem {
    
    private final NamedContainer element;
    
    private OutlineItem(NamedContainer element) {
        this.element = element;
    }
    
    public List<SidebarItem> children() {
        return itemsFrom(element.getChildren());
    }
    
    public SidebarIcon icon() {
        return null;
    }
    
    public String title() {
        return element.getName();
    }
    
    static List<SidebarItem> itemsFrom(List<Element> children) {
        List<SidebarItem> items = newLinkedList();
        
        for (Element child : children)
            if (child instanceof NamedContainer && !(child instanceof Task))
                items.add(new OutlineItem((NamedContainer) child));
        
        return items;
    }
}
