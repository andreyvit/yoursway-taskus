package com.mkalugin.lighthouse;

import static com.google.common.collect.Lists.newArrayList;

import java.net.URL;
import java.util.Collection;
import java.util.List;

import com.kalugin.plugins.sync.api.Source;
import com.kalugin.plugins.sync.api.SourceCallback;
import com.kalugin.plugins.sync.api.SourceQueryFailed;
import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTag;
import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTask;
import com.kalugin.plugins.sync.api.synchronizer.changes.Change;
import com.kalugin.plugins.sync.api.synchronizer.changes.ChangeVisitor;
import com.mkalugin.lighthouse.model.Project;
import com.mkalugin.lighthouse.model.Ticket;

public class LighthouseSource implements Source {

	private final URL url;
	private final String userName;
	private final String projectName;
	private final String query;
	private Lighthouse lighthouse;
	private Project project;

	public LighthouseSource(URL url, String userName, String projectName, String query) {
		if (url == null)
			throw new NullPointerException("url is null");
		if (userName == null)
			throw new NullPointerException("userName is null");
		if (projectName == null)
			throw new NullPointerException("projectName is null");
		if (query == null)
			throw new NullPointerException("listName is null");
		this.url = url;
		this.userName = userName;
		this.projectName = projectName;
		this.query = query;
	}

	private void createLighthouseConnector(SourceCallback callback, boolean force) {
		String password = callback.askPassword(url.getHost(), userName, force);
		lighthouse = new Lighthouse(url, userName, password);
	}

	public List<SynchronizableTask> computeTasks(SourceCallback callback) {
		try {
			boolean force = false;
			while (true) {
				createLighthouseConnector(callback, force);
				try {
					return doComputeTasks();
				} catch (LighthouseAuthenticationException e) {
				}
				System.out.println("Retrying authentication");
				force = true;
			}
		} catch (LighthouseException e) {
			throw new SourceQueryFailed(e);
		}
	}

	private List<SynchronizableTask> doComputeTasks() throws LighthouseException {
		project = chooseProject(lighthouse.listProjects());
		if (project != null) {
			Collection<Ticket> tickets = lighthouse.readTickets(project, query);
			List<SynchronizableTask> tasks = newArrayList();
			for (Ticket item : tickets)
				tasks.add(new LighthouseTicket(item, idTagName()));
			return tasks;
		}
		return newArrayList();
	}

	private Project chooseProject(Collection<Project> projects) {
		for (Project project : projects)
			if (project.getName().equals(projectName))
				return project;
		return null;
	}

	public String idTagName() {
		return "ticket";
	}

	public String identifier() {
		return "Lighthouse";
	}

	public void dispose() {
	}

	public void applyChanges(Collection<Change> changes, SourceCallback callback) {
		createLighthouseConnector(callback, false);
		doApplyChanges(changes);
	}

	private void doApplyChanges(Collection<Change> changes) {
		verifySource();
		for (Change change : changes)
			change.accept(new ChangeVisitor() {

				public void visitAddition(SynchronizableTask task) {
					try {
						lighthouse.createTicket(project, task.getName(), task.tags());
					} catch (LighthouseException e) {
						throw new SourceQueryFailed(e);
					}
				}

				public void visitRemoval(SynchronizableTask task) {
					try {
						LighthouseTicket ticket = (LighthouseTicket) task;
						lighthouse.deleteItem(ticket.item());
					} catch (LighthouseException e) {
						throw new SourceQueryFailed(e);
					}
				}

				public void visitRename(SynchronizableTask olderTask, SynchronizableTask newerTask) {
					try {
						LighthouseTicket btask = (LighthouseTicket) olderTask;
						lighthouse.rename(btask.item(), newerTask.getName());
					} catch (LighthouseException e) {
						throw new SourceQueryFailed(e);
					}
				}

				public void visitTagAddition(SynchronizableTask task, SynchronizableTag tag) {
					try {
						lighthouse.addTag(((LighthouseTicket) task).item(), tag.getName(), tag
								.getValue());
					} catch (LighthouseException e) {
						throw new SourceQueryFailed(e);
					}
				}

				public void visitTagRemoval(SynchronizableTask task, SynchronizableTag tag) {
					try {
						lighthouse.removeTag(((LighthouseTicket) task).item(), tag.getName());
					} catch (LighthouseException e) {
						throw new SourceQueryFailed(e);
					}
				}

				public void visitTagValueChange(SynchronizableTask task,
						SynchronizableTag olderTag, SynchronizableTag newerTag) {
					try {
						if (!olderTag.getName().equals(newerTag.getName())) {
							throw new IllegalArgumentException("olderTag.name != newerTag.name");
						}
						lighthouse.updateTagValue(((LighthouseTicket) task).item(), olderTag
								.getName(), newerTag.getValue());
					} catch (LighthouseException e) {
						throw new SourceQueryFailed(e);
					}
				}

			});
	}

	private void verifySource() {
		if (project == null)
			throw new SourceQueryFailed("No such project: " + projectName);
	}

}
