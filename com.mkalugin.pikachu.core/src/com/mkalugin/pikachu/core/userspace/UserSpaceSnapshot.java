package com.mkalugin.pikachu.core.userspace;

import com.mkalugin.pikachu.core.model.ModelSnapshot;

public class UserSpaceSnapshot implements ModelSnapshot {

	private final String content;
	private final long time;

	UserSpaceSnapshot(long time, String content) {
		this.time = time;
		this.content = content;
	}

	public String content() {
		return content;
	}

	public long timeStamp() {
		return time;
	}

}
