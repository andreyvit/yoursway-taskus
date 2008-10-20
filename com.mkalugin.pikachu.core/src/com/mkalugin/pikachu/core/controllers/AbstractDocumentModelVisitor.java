package com.mkalugin.pikachu.core.controllers;

import com.mkalugin.pikachu.core.model.document.Chapter;
import com.mkalugin.pikachu.core.model.document.Directive;
import com.mkalugin.pikachu.core.model.document.DocumentModelVisitor;
import com.mkalugin.pikachu.core.model.document.Element;
import com.mkalugin.pikachu.core.model.document.Group;
import com.mkalugin.pikachu.core.model.document.Section;
import com.mkalugin.pikachu.core.model.document.Tag;
import com.mkalugin.pikachu.core.model.document.Task;
import com.mkalugin.pikachu.core.model.document.TextLine;

public abstract class AbstractDocumentModelVisitor implements DocumentModelVisitor {
    
    protected abstract void visitElement(Element element);
    
    public void visit(Chapter chapter) {
        visitElement(chapter);
    }
    
    public void visit(Section section) {
        visitElement(section);
    }
    
    public void visit(Group group) {
        visitElement(group);
    }
    
    public void visit(Task task) {
        visitElement(task);
    }
    
    public void visit(TextLine line) {
        visitElement(line);
    }
    
    public void visit(Tag tag) {
        visitElement(tag);
    }
    
    public void visit(Directive directive) {
        visitElement(directive);
    }
    
}
