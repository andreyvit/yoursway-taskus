package com.mkalugin.pikachu.core.storage;


public interface DataStorageBuilder {

	DataStorage buildFrom(String memento) throws IllegalArgumentException;
	
}
