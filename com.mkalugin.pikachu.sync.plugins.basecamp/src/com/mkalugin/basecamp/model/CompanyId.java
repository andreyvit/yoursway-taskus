package com.mkalugin.basecamp.model;

public class CompanyId extends AbstractId implements ReponsiblePartyId {
    
    public CompanyId(int id) {
        super(id);
    }

    public String getResponsiblePartyForToDoItemUpdate() {
        return "c" + numericId();
    }
    
}
