package com.mkalugin.pikachu.core.controllers.search;

public interface SearchResult {

	int matchesCount();

	SearchMatch getMatchWithNumber(int num);

}