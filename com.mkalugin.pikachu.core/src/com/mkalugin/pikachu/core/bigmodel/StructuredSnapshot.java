package com.mkalugin.pikachu.core.bigmodel;

import com.mkalugin.pikachu.core.model.ModelSnapshot;

public class StructuredSnapshot implements ModelSnapshot {

	private final long time;
	private final String content;
	private final String[] titles;

	public StructuredSnapshot(long time, String content, String[] titles) {
		this.time = time;
		this.content = content;
		this.titles = titles;
	}

	public String[] ast() {
		return titles;
	}
	
	public String content() {
		return content;
	}

	public long timeStamp() {
		return time;
	}

}
