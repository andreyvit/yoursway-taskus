package com.mkalugin.pikachu.core.model.document;

public class Section extends NamedContainer {
    
    private final Token index;
    
    public Section(Token name, Token index, int start, int end) {
        super(name, start, end);
        
        if (index == null)
            throw new NullPointerException("index is null");
        
        this.index = index;
    }
    
    private int level() {
        String text = index.getText();
        String[] split = text.split("\\.");
        return split.length;
    }
    
    public boolean doesChildMatch(Element child) {
        if (child instanceof Section) {
            int level = ((Section) child).level();
            int level2 = level();
            return level > level2;
        }
        
        return !(child instanceof Chapter);
    }
    
    @Override
    public void accept(DocumentModelVisitor visitor) {
        visitor.visit(this);
        super.accept(visitor);
    }
    
}
