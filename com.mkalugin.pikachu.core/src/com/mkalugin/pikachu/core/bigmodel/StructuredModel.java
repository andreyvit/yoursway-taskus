package com.mkalugin.pikachu.core.bigmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mkalugin.pikachu.core.model.AbstractModel;
import com.mkalugin.pikachu.core.model.ModelConsumer;
import com.mkalugin.pikachu.core.userspace.UserSpace;
import com.mkalugin.pikachu.core.userspace.UserSpaceSnapshot;

public class StructuredModel extends AbstractModel<StructuredSnapshot> implements ModelConsumer<UserSpaceSnapshot> {
	
	private static final Pattern pattern = Pattern.compile("^.*:$", Pattern.MULTILINE);

	private StructuredSnapshot lastSnapshot;
	private final UserSpace userSpace;

	public StructuredModel(UserSpace userSpace) {
		this.userSpace = userSpace;
		this.userSpace.registerConsumer(this);
	}

	public void consume(UserSpaceSnapshot snapshot) {
		synchronized (lastSnapshot) {
			lastSnapshot = buildNewSnapshot(snapshot);
			notifyConsumers(lastSnapshot);
		}
	}
	
	protected StructuredSnapshot buildNewSnapshot(UserSpaceSnapshot source) {
		return new StructuredSnapshot(source, parse(source.content()));
	}

	protected String[] parse(String content) {
		List<String> result = new ArrayList<String>();
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			result.add(matcher.group(0));
		}
		return result.toArray(new String[result.size()]);
	}
	
}
