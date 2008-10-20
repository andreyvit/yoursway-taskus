package com.mkalugin.pikachu.core.model.document;

public class Chapter extends NamedContainer {
    
    public Chapter(Token name, int start, int end) {
        super(name, start, end);
    }
    
    public boolean doesChildMatch(Element child) {
        return !(child instanceof Chapter);
    }
    
    @Override
    public void accept(DocumentModelVisitor visitor) {
        visitor.visit(this);
        super.accept(visitor);
    }
    
}
