package com.mkalugin.pikachu.core.controllers.search;

public interface SearchCallback {

	void setSearchPattern(String pattern);
	
	void nextMatch();
	
	void previousMatch();
	
}
