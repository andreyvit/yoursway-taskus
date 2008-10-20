package com.mkalugin.pikachu.core.model.document;

public interface DocumentModelVisitor {
    
    void visit(Chapter chapter);
    
    void visit(Section section);
    
    void visit(Group group);
    
    void visit(Task task);
    
    void visit(TextLine line);
    
    void visit(Tag tag);
    
    void visit(Directive directive);
    
}
