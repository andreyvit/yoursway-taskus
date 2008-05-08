/**
 * 
 */
package com.mkalugin.pikachu.core.workspace;

import java.util.HashMap;
import java.util.Map;

public class ChangeSet {

	private Map<String, String> newState = new HashMap<String, String>();

	public void add(String path, String content) {
		newState.put(path, content);
	}

}