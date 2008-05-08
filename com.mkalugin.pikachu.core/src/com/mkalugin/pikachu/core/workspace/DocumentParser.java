package com.mkalugin.pikachu.core.workspace;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DocumentParser {

	private static final Pattern pattern = Pattern.compile("^(.*):$", Pattern.MULTILINE);

	public DocumentParser() {
	}

	public String[] parse(String source) {
		List<String> result = new ArrayList<String>();
		Matcher matcher = pattern.matcher(source);
		while (matcher.find()) {
			result.add(matcher.group(1));
		}
		return result.toArray(new String[result.size()]);
	}

}
