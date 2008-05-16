package com.mkalugin.pikachu.core.controllers.search;

import java.util.ArrayList;
import java.util.List;

class StandardSearchResult implements SearchResult {

	private final List<SearchMatch> matches;

	StandardSearchResult() {
		this.matches = new ArrayList<SearchMatch>();
	}
	
	public int matchesCount() {
		return matches.size();
	}

	public SearchMatch getMatchWithNumber(int num) {
		return matches.get(num);
	}
	
	void addMatch(SearchMatch match) {
		matches.add(match);
	}

}
