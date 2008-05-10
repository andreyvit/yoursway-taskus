package com.mkalugin.pikachu.core.ast;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

public class ATag extends ANode {

	private final ATagName name;
    private final ATagValue value;

    public ATag(int start, int end, ATagName name, ATagValue value) {
	    super(start, end);
	    if (name == null)
            throw new NullPointerException("name is null");
        this.name = name;
        this.value = value;
	}

	@Override
	public String toString() {
		return containerToString(children());
	}

    private List<ANode> children() {
        List<ANode> list = newArrayList((ANode) name);
		if (value != null)
		    list.add(value);
        return list;
    }

}
