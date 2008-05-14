package com.mkalugin.pikachu.core.model.document.structure;

public class MProject extends MHernyaContainer {

    private String name;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    protected String containerDescription() {
        return super.containerDescription() + " " + name;
    }

}
