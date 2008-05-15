package com.mkalugin.pikachu.core.controllers.search;

import com.mkalugin.pikachu.core.DocumentListener;
import com.mkalugin.pikachu.core.model.Document;

public class SearchController implements SearchCallback {

	private static class NullSearchResult implements SearchResult {

		public SearchMatch getMatchWithNumber(int num) {
			return null;
		}

		public int matchesCount() {
			return 0;
		}

	}

	private static SearchResult NULL_RESULT = new NullSearchResult();

	private SearchControls controls;
	private final Document document;

	private SearchResult currentResult;
	private int currentHighlightedMatch;

	public SearchController(Document document, SearchControlsFactory binding) {
		if (document == null)
			throw new IllegalArgumentException("document is null");
		if (binding == null)
			throw new IllegalArgumentException("binding is null");
		this.document = document;
		document.addListener(new DocumentListener() {

			public void bindingChanged() {
			}

			public void closed(boolean discarded) {
			}

			public void contentChanged(Object sender) {
				controls.clearSearchField();
			}

			public void emptinessChanged() {
			}
			
		});
		controls = binding.bindSearchControls(this);
	}

	public void nextMatch() {
		if (currentResult == null)
			throw new IllegalStateException();
		if (currentHighlightedMatch < currentResult.matchesCount() - 1) {
			++currentHighlightedMatch;
			controls.hightlightMatch(currentHighlightedMatch);
		}
	}

	public void previousMatch() {
		if (currentResult == null)
			throw new IllegalStateException();
		if (currentHighlightedMatch > 0) {
			--currentHighlightedMatch;
			controls.hightlightMatch(currentHighlightedMatch);
		}
	}

	public void setSearchPattern(String pattern) {
		if (pattern == null || pattern.length() == 0) {
			controls.setMatchesNavigationEnabled(false);
			currentResult = NULL_RESULT;
		} else {
			controls.setMatchesNavigationEnabled(true);
			currentResult = performSearch(pattern);
		}
		currentHighlightedMatch = 0;
		controls.showSearchResult(currentResult);
	}

	private SearchResult performSearch(String pattern) {
		pattern = pattern.toLowerCase();
		String content = document.getContent().toLowerCase();
		StandardSearchResult result = new StandardSearchResult(pattern);
		int patternLength = pattern.length();
		int pos, lastMatchEnd = -1;
		while (-1 != (pos = content.indexOf(pattern, lastMatchEnd + 1))) {
			lastMatchEnd = pos + patternLength - 1;
			result.addMatch(new SearchMatch(pos, lastMatchEnd));
		}
		return result;
	}

}
