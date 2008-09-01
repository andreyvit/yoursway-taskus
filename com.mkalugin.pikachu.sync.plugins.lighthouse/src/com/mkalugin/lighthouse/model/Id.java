package com.mkalugin.lighthouse.model;

public class Id {
	
	public static Id id(Integer i) {
		return (i == null)?null:new Id(i);
	}

	private final String id;

	public Id(String id) {
		this.id = id;
	}

	public Id(int id) {
		this.id = "" + id;
	}

	public int integer() {
		return Integer.parseInt(id);
	}
	
	@Override
	public String toString() {
		return id;
	}

}
