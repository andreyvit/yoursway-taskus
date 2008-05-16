package com.mkalugin.pikachu.core.controllers.search;

public interface SearchControls {

	void clearSearchField();
	
	void setMatchesNavigationEnabled(boolean enabled);
	
	void showSearchResult(SearchResult result);
	
	void hightlightMatch(int number);
	
	void switchFocusToEditor();

	void setEditorSelectionTo(SearchMatch matchWithNumber);
	
}
