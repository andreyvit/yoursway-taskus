package com.mkalugin.pikachu.core.controllers;

import static com.google.common.collect.Iterables.addAll;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static com.mkalugin.pikachu.core.controllers.sync.TaskPersistance.parseTasks;
import static com.mkalugin.pikachu.core.controllers.sync.TaskPersistance.tasksToString;
import static com.yoursway.utils.YsDigest.sha1;
import static com.yoursway.utils.YsFileUtils.readAsString;
import static com.yoursway.utils.YsFileUtils.writeString;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.kalugin.plugins.sync.api.Source;
import com.kalugin.plugins.sync.api.SourceCallback;
import com.kalugin.plugins.sync.api.SourceQueryFailed;
import com.kalugin.plugins.sync.api.SyncPluginsRegistry;
import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTag;
import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTask;
import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTaskUtils;
import com.kalugin.plugins.sync.api.synchronizer.Synchronizer;
import com.kalugin.plugins.sync.api.synchronizer.changes.Change;
import com.kalugin.plugins.sync.api.synchronizer.local_changes.LocalChange;
import com.kalugin.plugins.sync.api.synchronizer.local_changes.LocalChangeVisitor;
import com.mkalugin.pikachu.core.ast.ADocument;
import com.mkalugin.pikachu.core.ast.AProjectName;
import com.mkalugin.pikachu.core.controllers.sync.LocalTag;
import com.mkalugin.pikachu.core.controllers.sync.LocalTask;
import com.mkalugin.pikachu.core.controllers.sync.rewriting.RewritingSession;
import com.mkalugin.pikachu.core.controllers.viewglue.DocumentWindow;
import com.mkalugin.pikachu.core.model.Document;
import com.mkalugin.pikachu.core.model.document.structure.MDocument;
import com.mkalugin.pikachu.core.model.document.structure.MElement;
import com.mkalugin.pikachu.core.model.document.structure.MProject;
import com.mkalugin.pikachu.core.model.document.structure.MTag;
import com.mkalugin.pikachu.core.model.document.structure.MTask;
import com.mkalugin.pikachu.core.model.document.structure.MText;
import com.mkalugin.pikachu.core.model.document.structure.builder.StructuredModelBuilder;

public class SynchronizationController {

	private final Document document;

	private RewritingSession session;

	private final SourceCallback callback;

	public SynchronizationController(Document document, SourceCallback callback) {
		if (document == null)
			throw new NullPointerException("document is null");
		if (callback == null)
			throw new NullPointerException("callback is null");
		this.document = document;
		this.callback = callback;
	}

	public void run(DocumentWindow window, AProjectName projectName) {
		ADocument ast = document.getDocumentNode();
		session = new RewritingSession(document.getContent());
		MDocument structure = new StructuredModelBuilder().buildStructure(ast);
		for (MElement element : structure.getChildren())
			if (element instanceof MProject) {
				MProject project = (MProject) element;
				if (projectName != null) {
					if (projectName.equals(project.getLine().name())) {
						callback.setProgressMessage("Synchronizing project " + project.getName());
						process(project);
						break;
					}
				} else {
					callback.setProgressMessage("Synchronizing project " + project.getName());
					process(project);
				}
			}
		window.runSync(new Runnable() {

			public void run() {
				document.setContent(session.commit(), this);
			}

		});
	}

	public static void collectSynchronizationDefinitions(MProject project,
			List<SynchronizationDefinition> synchronizationDefinitions) {
		for (MElement element : project.getChildren())
			if (element instanceof MText) {
				String text = ((MText) element).getText();
				Source source = SyncPluginsRegistry.forPhrase(text);
				if (source != null)
					synchronizationDefinitions.add(new SynchronizationDefinition((MText) element,
							source));
			}
	}

	private void process(MProject project) {
		// name intentionally inverted ;-)
		File muflaFufla = new File(document.fuflaMufla(), sha1(project.getName()));

		List<SynchronizationDefinition> synchronizationDefinitions = newArrayList();
		collectSynchronizationDefinitions(project, synchronizationDefinitions);

		if (!synchronizationDefinitions.isEmpty())
			synchronize(project, synchronizationDefinitions, muflaFufla);
	}

	private void synchronize(MProject project, List<SynchronizationDefinition> definitions,
			File muflaFufla) {
		for (SynchronizationDefinition definition : definitions)
			try {
				synchronize(project, definition, muflaFufla);
			} catch (SyncronizationCancelledException e) {
			} catch (SourceQueryFailed e) {
				Throwable initial = e;
				while (true) {
					Throwable cause = initial.getCause();
					if (cause == null || cause == initial)
						break;
					initial = cause;
				}
				callback.abortWithMessage(initial.getMessage());
			} catch (Exception e) {
				callback.abortWithMessage("Totally unexpected exception: " + e.getMessage());
			}
	}

	private void synchronize(MProject project, final SynchronizationDefinition definition,
			File animals) {
		Source source = definition.getSource();
		System.out.println("Synchronizing " + project.getName() + " with " + source);

		File pigsThreeDifferentOnes = new File(animals, source.identifier());
		File nafNafWithRedTail /* color coding */= new File(pigsThreeDifferentOnes, "naf-naf");
		File nifNifWithGreenTail = new File(pigsThreeDifferentOnes, "nif-nif");
		File nufNuf = new File(pigsThreeDifferentOnes, "nuf-nuf"); // just for
																	// symmetry

		Synchronizer synchronizer = new Synchronizer();

		List<LocalTask> localTasksObsessedWithRed = newArrayList();
		for (MElement element : project.getChildren())
			collectLocalTasks(element, localTasksObsessedWithRed, source);

		List<SynchronizableTask> remoteTasksObsessedWithGreen = source.computeTasks(callback);
		synchronizer.setNewRemoteTasks(remoteTasksObsessedWithGreen);

		synchronizer.setNewLocalTasks(localTasksObsessedWithRed);

		if (nafNafWithRedTail.exists() && nifNifWithGreenTail.exists())
			try {
				String idTagName = source.idTagName();
				synchronizer
						.setOldLocalTasks(parseTasks(readAsString(nafNafWithRedTail), idTagName));
				synchronizer.setOldRemoteTasks(parseTasks(readAsString(nifNifWithGreenTail),
						idTagName));

				// the pigs are aging, kill them
				nafNafWithRedTail.delete();
				nifNifWithGreenTail.delete();
			} catch (IOException e) {
				e.printStackTrace();
			}

		Collection<Change> changesToApplyRemotely = synchronizer.synchronizeRemote();
		source.applyChanges(changesToApplyRemotely, callback);
		if (!changesToApplyRemotely.isEmpty()) {
			// recompute the actual state of remote tasks
			synchronizer.setOldRemoteTasks(remoteTasksObsessedWithGreen);
			remoteTasksObsessedWithGreen = source.computeTasks(callback);
			synchronizer.setNewRemoteTasks(remoteTasksObsessedWithGreen);
		}

		synchronizer.setOldLocalTasks(localTasksObsessedWithRed); // special street magic
		
		Collection<LocalChange> changesToApplyLocally = synchronizer.synchronizeLocal();
		for (LocalChange change : changesToApplyLocally)
			change.accept(new ChangeApplicator(definition, localTasksObsessedWithRed));
		source.dispose();

		try {
			pigsThreeDifferentOnes.mkdirs();
			writeString(nafNafWithRedTail, tasksToString(localTasksObsessedWithRed));
			writeString(nifNifWithGreenTail, tasksToString(remoteTasksObsessedWithGreen));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void collectLocalTasks(MElement element, List<LocalTask> localTasks, Source source) {
		if (element instanceof MTask)
			collectLocalTasks_((MTask) element, localTasks, source);
	}

	private void collectLocalTasks_(MTask element, List<LocalTask> localTasks, Source source) {
		localTasks.add(new LocalTask(element, source.idTagName()));
	}

	private final class ChangeApplicator implements LocalChangeVisitor {
		private final SynchronizationDefinition definition;
		private final List<LocalTask> localTasks;

		private ChangeApplicator(SynchronizationDefinition definition, List<LocalTask> localTasks) {
			if (definition == null)
				throw new NullPointerException("definition is null");
			if (localTasks == null)
				throw new NullPointerException("localTasks is null");
			this.definition = definition;
			this.localTasks = localTasks;
		}

		public void visitAddition(SynchronizableTask task) {
			MTask newTask = wrap(task);
			session.addTask(newTask, definition.instruction);
//			localTasks.add(new LocalTask(newTask, definition.source.idTagName()));
		}

		public void visitTaskRemoval(SynchronizableTask task) {
			session.removeTask(((LocalTask) task).getTask().getNode());
//			localTasks.remove(task);
		}

		public void visitRename(SynchronizableTask olderTask, SynchronizableTask newerTask) {
			session.renameTask(((LocalTask) olderTask).getTask().getNode(), wrap(newerTask));
//			localTasks.remove(olderTask);
//			localTasks.add((LocalTask) newerTask);
		}

		public void visitTagAddition(SynchronizableTask task, SynchronizableTag tag) {
			session.insertTag(((LocalTask) task).getTask(), wrap(tag));
			MTask newTask = wrap(task);
			newTask.addTag(wrap(tag));
//			localTasks.remove(task);
//			localTasks.add(new LocalTask(newTask, definition.source.idTagName()));
		}

		public void visitTagRemoval(SynchronizableTask task, SynchronizableTag tag) {
			session.removeTag(((LocalTag) tag).getTag());
			
//			MTask newTask = wrap(task);
//			newTask.removeTag(wrap(tag));
//			localTasks.remove(task);
//			localTasks.add(new LocalTask(newTask, definition.source.idTagName()));
//			
		}

		public void visitTagValueChange(SynchronizableTask task, SynchronizableTag olderTag,
				SynchronizableTag newerTag) {
			session.changeTagValue(((LocalTag) olderTag).getTag(), wrap(newerTag));
			
//			MTask newTask = wrap(task);
//			newTask.removeTag(wrap(olderTag));
//			newTask.addTag(wrap(newerTag));
//			localTasks.remove(task);
//			localTasks.add(new LocalTask(newTask, definition.source.idTagName()));
//			
		}

		private boolean falseIgnore(SynchronizableTask remoteTask) {
			tasksLoop: for (LocalTask task:  localTasks) {
				if (!remoteTask.getName().equals(task.getName()))
	        		continue;
	        	Collection<SynchronizableTag> tags1 = remoteTask.tags();
	        	Collection<SynchronizableTag> tags2 = task.tags();
	        	if (tags1.size() != tags2.size())
	        		continue;
	        	HashSet<String> tagNames = newHashSet();
	        	addAll(tagNames, transform(tags1, SynchronizableTaskUtils.TAG_TO_NAME));
	        	for (String name : transform(tags2, SynchronizableTaskUtils.TAG_TO_NAME)) {
	        		if (!tagNames.contains(name) && !name.equals(definition.source.idTagName())) {
	        			continue tasksLoop;
	        		}
	        	}
	        	return true;
			}
			return false;
			
		}
		
		public void visitIgnoredTaskAddition(SynchronizableTask remoteTask) {
			if (falseIgnore(remoteTask))
				return;
			
			MTag tag = new MTag();
			tag.setName("ignore");
			MTask newTask = wrap(remoteTask);
			newTask.addTag(tag);
			session.addTask(newTask, definition.instruction);
		}

		private MTask wrap(SynchronizableTask task) {
			MTask newTask = new MTask();
			newTask.setName(task.getName());
			for (SynchronizableTag tag : task.tags())
				newTask.addTag(wrap(tag));
			return newTask;
		}

		private MTag wrap(SynchronizableTag tag) {
			MTag result = new MTag();
			result.setName(tag.getName());
			result.setValue(tag.getValue());
			return result;
		}

	}

	static class SynchronizationDefinition {

		private final Source source;
		private final MText instruction;

		public SynchronizationDefinition(MText instruction, Source source) {
			if (instruction == null)
				throw new NullPointerException("instruction is null");
			if (source == null)
				throw new NullPointerException("source is null");
			this.instruction = instruction;
			this.source = source;
		}

		public MText getInstruction() {
			return instruction;
		}

		public Source getSource() {
			return source;
		}

	}

}
