package com.mkalugin.pikachu.core.model;

import java.util.ArrayList;
import java.util.List;

public class AbstractModel<T extends ModelSnapshot> implements Model<T> {
	
	private List<ModelConsumer<T>> consumers = new ArrayList<ModelConsumer<T>>();

	public void registerConsumer(ModelConsumer<T> consumer) {
		consumers.add(consumer);
	}

	public void unregisterConsumer(ModelConsumer<T> consumer) {
		consumers.add(consumer);
	}
	
	protected void notifyConsumers(T snapshot) {
		synchronized (consumers) {
			for (ModelConsumer<T> c : consumers) {
				c.consume(snapshot);
			}			
		}
	}

}
