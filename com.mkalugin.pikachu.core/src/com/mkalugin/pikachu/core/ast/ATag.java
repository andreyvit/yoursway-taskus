package com.mkalugin.pikachu.core.ast;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

public class ATag extends ANodeImpl implements ATaskLevelNode {

	private final ATagName name;
    private final ATagValue value;

    public ATag(int start, int end, ATagName name, ATagValue value) {
	    super(start, end);
	    if (name == null)
            throw new NullPointerException("name is null");
        this.name = name;
        this.value = value;
	}
    
    public ATagName getName() {
        return name;
    }
    
    public ATagValue getValue() {
        return value;
    }

	@Override
	public String toString() {
		return containerToString(children());
	}

    private List<ANodeImpl> children() {
        List<ANodeImpl> list = newArrayList((ANodeImpl) name);
		if (value != null)
		    list.add(value);
        return list;
    }

    public void accept(ATaskLevelVisitor visitor) {
        visitor.visitTag(this);
    }

    public String nameAsString() {
        return name.getText();
    }

}
