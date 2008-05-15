/**
 * 
 */
package com.mkalugin.pikachu.core.controllers.search;

public class SearchMatch {

	private final int startOffset;
	private final int endOffset;

	public SearchMatch(int startOffset, int endOffset) {
		this.endOffset = endOffset;
		this.startOffset = startOffset;
	}

	public int startOffset() {
		return startOffset;
	}

	public int endOffset() {
		return endOffset;
	}

}