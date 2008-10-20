package com.mkalugin.pikachu.core.model.document;

public class Group extends NamedContainer {
    
    public Group(Token name, int start, int end) {
        super(name, start, end);
    }
    
    public boolean doesChildMatch(Element child) {
        return !(child instanceof Chapter || child instanceof Section || child instanceof Group);
    }
    
    @Override
    public void accept(DocumentModelVisitor visitor) {
        visitor.visit(this);
        super.accept(visitor);
    }
    
}
