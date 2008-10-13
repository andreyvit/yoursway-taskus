package com.mkalugin.pikachu.core.model.document;

import com.yoursway.utils.EventSource;
import com.yoursway.utils.broadcaster.Broadcaster;
import com.yoursway.utils.broadcaster.BroadcasterFactory;

public abstract class SimpleElement implements Element {
    
    private final Range range;
    Broadcaster<DocumentModelListener> broadcaster = BroadcasterFactory
            .newBroadcaster(DocumentModelListener.class);
    
    public SimpleElement(int start, int end) {
        range = new Range(start, end);
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
    
    public Range range() {
        return range;
    }
    
    public EventSource<DocumentModelListener> events() {
        return broadcaster;
    }
    
    protected void changed() {
        broadcaster.fire().documentChanged(this);
    }
    
}
