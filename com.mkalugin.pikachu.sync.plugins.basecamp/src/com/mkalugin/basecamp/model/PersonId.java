package com.mkalugin.basecamp.model;

public class PersonId extends AbstractId implements ReponsiblePartyId {
    
    public PersonId(int id) {
        super(id);
    }

    public String getResponsiblePartyForToDoItemUpdate() {
        return "" + numericId();
    }
    
}
