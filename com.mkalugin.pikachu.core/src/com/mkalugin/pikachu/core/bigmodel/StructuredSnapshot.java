package com.mkalugin.pikachu.core.bigmodel;

import com.mkalugin.pikachu.core.model.ModelSnapshot;
import com.mkalugin.pikachu.core.userspace.UserSpaceSnapshot;

public class StructuredSnapshot implements ModelSnapshot {

	private final UserSpaceSnapshot userSpace;
	private final String[] titles;

	public StructuredSnapshot(UserSpaceSnapshot base, String[] titles) {
		this.userSpace = base;
		this.titles = titles;
	}

	public String[] ast() {
		return titles;
	}
	
	public UserSpaceSnapshot userSpace() {
		return userSpace;
	}

	public long timeStamp() {
		return userSpace.timeStamp();
	}

}
