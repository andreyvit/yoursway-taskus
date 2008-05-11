package com.mkalugin.pikachu.core.ast;

public class ATaskLine extends AContainer<ATaskLevelNode> implements ADocumentLevelNode {
    
	public ATaskLine(int start, int end) {
	    super(start, end);
	}

    public void accept(ADocumentLevelVisitor visitor) {
        visitor.visitTaskLine(this);
    }

}
