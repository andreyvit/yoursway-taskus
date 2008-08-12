package com.mkalugin.pikachu.core.model.document.structure;

import com.mkalugin.pikachu.core.ast.AProjectLine;

public class MProject extends MHernyaContainer {

    private String name;
    private AProjectLine line;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public AProjectLine getLine() {
		return line;
	}
    
    public void setLine(AProjectLine line) {
		this.line = line;
	}
    
    @Override
    protected String containerDescription() {
        return super.containerDescription() + " " + name;
    }

}
