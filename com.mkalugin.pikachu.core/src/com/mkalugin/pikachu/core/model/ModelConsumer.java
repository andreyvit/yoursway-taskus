package com.mkalugin.pikachu.core.model;


public interface ModelConsumer<T> {

	void consume(T snapshot);
	
}
