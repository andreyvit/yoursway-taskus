package com.mkalugin.lighthouse;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.List;

import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTag;
import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTask;
import com.kalugin.plugins.sync.api.synchronizer.TaskId;
import com.mkalugin.lighthouse.model.Ticket;

public class LighthouseTicket implements SynchronizableTask {

	private final Ticket item;
	private LighthouseTag idTag;

	public LighthouseTicket(Ticket item, String idTagName) {
		if (item == null)
			throw new NullPointerException("item is null");
		if (idTagName == null)
			throw new NullPointerException("idTagName is null");
		this.item = item;
		idTag = new LighthouseTag(idTagName, "" + item.getNumber());
	}

	public TaskId getId() {
		return new TaskId("" + item.getNumber());
	}

	public String getName() {
		return item.getTitle();
	}

	public Ticket item() {
		return item;
	}

	public Collection<SynchronizableTag> tags() {
		List<SynchronizableTag> result = newArrayList();
		result.add(idTag);
		if (item.getMilestone() != null)
			result.add(new LighthouseTag("milestone", item.getMilestone().getTitle()));
		if (item.getState() != null)
			result.add(new LighthouseTag("state", item.getState()));
		if (item.getAssignedUser() != null)
			result.add(new LighthouseTag("assignee", item.getAssignedUser().getName()));
		String tags = item.getTags();
		if (tags != null) {
			String[] split = tags.split(" ");
			for (String s : split) {
				if (s.trim().length() == 0)
					continue;
				result.add(new LighthouseTag(s, null));
			}
		}
		return result;
	}

	public String toStringWithoutTags() {
		return item.getTitle() + " #" + item.getNumber();
	}

	public boolean wannaBeAdded() {
		return false;
	}

}
