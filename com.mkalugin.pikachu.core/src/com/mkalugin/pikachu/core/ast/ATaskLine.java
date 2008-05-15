package com.mkalugin.pikachu.core.ast;

public class ATaskLine extends AContainer<ATaskLevelNode> implements ADocumentLevelNode {
    
	private final class IsDoneVisitor implements ATaskLevelVisitor {
	    
	    private boolean isDone = false;
	    
        public void visitDescriptionFragment(ATaskDescriptionFragment fragment) {
        }
        
        public void visitLeader(ATaskLeader leader) {
        }
        
        public void visitName(ATaskName name) {
        }
        
        public void visitTag(ATag tag) {
            if (tag.getName().getText().equals("done"))
                isDone = true;
        }
        
        public boolean isDone() {
            return isDone;
        }
        
    }

    public ATaskLine(int start, int end) {
	    super(start, end);
	}

    public void accept(ADocumentLevelVisitor visitor) {
        visitor.visitTaskLine(this);
    }
    
    public ATaskName name() {
        for(ATaskLevelNode node : getChildren())
            if (node instanceof ATaskName)
                return (ATaskName) node;
        return null;
    }
    
    public boolean isDone() {
        IsDoneVisitor visitor = new IsDoneVisitor();
        for(ATaskLevelNode child : getChildren())
            child.accept(visitor);
        return visitor.isDone();
    }

}
