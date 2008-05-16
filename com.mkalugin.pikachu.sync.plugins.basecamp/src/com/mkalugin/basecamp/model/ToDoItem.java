package com.mkalugin.basecamp.model;

public class ToDoItem {

    private final int id;
    private final int position;
    private final String content;
    private final boolean completed;
    private final ReponsiblePartyId responsiblePartyId;

    public ToDoItem(int id, int position, boolean completed, String content, ReponsiblePartyId responsiblePartyId) {
        this.completed = completed;
        this.responsiblePartyId = responsiblePartyId;
        if (content == null)
            throw new NullPointerException("content is null");
        this.id = id;
        this.position = position;
        this.content = content;
    }
    
    public int getId() {
        return id;
    }
    
    public int getPosition() {
        return position;
    }
    
    public boolean isCompleted() {
        return completed;
    }
    
    public String getContent() {
        return content;
    }
    
    public ReponsiblePartyId getResponsiblePartyId() {
        return responsiblePartyId;
    }
    
}
