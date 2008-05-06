package com.mkalugin.pikachu.core.bigmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mkalugin.pikachu.core.model.AbstractModel;
import com.mkalugin.pikachu.core.model.ModelConsumer;
import com.mkalugin.pikachu.core.storage.Commit;
import com.mkalugin.pikachu.core.storage.DataStorage;
import com.mkalugin.pikachu.core.storage.StorageException;
import com.mkalugin.pikachu.core.storage.StorageSnapshot;

public class StructuredModel extends AbstractModel<StructuredSnapshot> implements
		ModelConsumer<StorageSnapshot> {

	private static final Pattern pattern = Pattern.compile("^.*:$", Pattern.MULTILINE);

	private StructuredSnapshot lastSnapshot;
	private final DataStorage userSpace;

	public StructuredModel(DataStorage userSpace) {
		this.userSpace = userSpace;
		this.userSpace.registerConsumer(this);
	}

	protected void updateTo(StorageSnapshot source) {
		String mainFile = source.contentsOfFile("data.txt");
		pushSnapshot(source.timeStamp(), mainFile);
	}

	protected synchronized void pushSnapshot(long time, String mainFile) {
		if (lastSnapshot == null || !mainFile.equals(lastSnapshot.content())) {
			lastSnapshot = new StructuredSnapshot(time, mainFile, parse(mainFile));
			notifyConsumers(lastSnapshot);
		}
	}

	protected String[] parse(String content) {
		List<String> result = new ArrayList<String>();
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			result.add(matcher.group(0));
		}
		return result.toArray(new String[result.size()]);
	}

	public void consume(StorageSnapshot snapshot) {
		updateTo(snapshot);
	}

	public void pushData(String data) throws StorageException {
		Commit commit = userSpace.commit();
		commit.add("data.txt", data);
		commit.apply();
		pushSnapshot(System.currentTimeMillis(), data);
	}

	public void synchronize() {

	}

}
