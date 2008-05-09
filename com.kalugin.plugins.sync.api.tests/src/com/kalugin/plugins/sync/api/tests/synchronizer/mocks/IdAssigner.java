package com.kalugin.plugins.sync.api.tests.synchronizer.mocks;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;

public class IdAssigner<T> {
    
    private Map<T, Integer> ids = newHashMap();
    
    private int nextId;
    
    private final int increment;
    
    public IdAssigner(int firstId, int increment) {
        this.nextId = firstId;
        this.increment = increment;
    }
    
    public IdAssigner() {
        this(1, 1);
    }
    
    public int idOf(T item) {
        Integer existingId = ids.get(item);
        if (existingId != null)
            return existingId;
        int id = nextId;
        ids.put(item, id);
        nextId += increment;
        return id;
    }
    
}
