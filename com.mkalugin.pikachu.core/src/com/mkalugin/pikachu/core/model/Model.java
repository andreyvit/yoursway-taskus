package com.mkalugin.pikachu.core.model;

public interface Model<T> {

	void registerConsumer(ModelConsumer<T> consumer);
	void unregisterConsumer(ModelConsumer<T> consumer);
	
}
