package com.mkalugin.pikachu.core.controllers;

import static com.google.common.collect.Lists.newArrayList;
import static com.mkalugin.pikachu.core.controllers.sync.TaskPersistance.parseTasks;
import static com.mkalugin.pikachu.core.controllers.sync.TaskPersistance.tasksToString;
import static com.yoursway.utils.YsDigest.sha1;
import static com.yoursway.utils.YsFileUtils.readAsString;
import static com.yoursway.utils.YsFileUtils.writeString;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.kalugin.plugins.sync.api.Source;
import com.kalugin.plugins.sync.api.SyncPluginsRegistry;
import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTag;
import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTask;
import com.kalugin.plugins.sync.api.synchronizer.SynchronizationResult;
import com.kalugin.plugins.sync.api.synchronizer.Synchronizer;
import com.kalugin.plugins.sync.api.synchronizer.TaskId;
import com.kalugin.plugins.sync.api.synchronizer.changes.ChangeVisitor;
import com.kalugin.plugins.sync.api.synchronizer.local_changes.LocalChange;
import com.kalugin.plugins.sync.api.synchronizer.local_changes.LocalChangeVisitor;
import com.mkalugin.pikachu.core.ast.ADocument;
import com.mkalugin.pikachu.core.controllers.sync.LocalTag;
import com.mkalugin.pikachu.core.controllers.sync.LocalTask;
import com.mkalugin.pikachu.core.controllers.sync.rewriting.RewritingSession;
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

    public SynchronizationController(Document document) {
        this.document = document;
    }
    
    public void run() {
        ADocument ast = document.getDocumentNode();
        session = new RewritingSession(document.getContent());
        MDocument structure = new StructuredModelBuilder().buildStructure(ast);
        for (MElement element : structure.getChildren())
            if (element instanceof MProject) {
                process((MProject) element);
            }
        document.setContent(session.commit(), this);
    }

    private void process(MProject project) {
        // name intentionally inverted ;-)
        File muflaFufla = new File(document.fuflaMufla(), sha1(project.getName()));
        
        List<SynchronizationDefinition> synchronizationDefinitions = newArrayList();
        for (MElement element : project.getChildren())
            if (element instanceof MText)
                process((MText) element, synchronizationDefinitions);
        
        if (!synchronizationDefinitions.isEmpty())
            synchronize(project, synchronizationDefinitions, muflaFufla);
    }
    
    private void synchronize(MProject project, List<SynchronizationDefinition> definitions, File muflaFufla) {
        for (SynchronizationDefinition definition : definitions) 
            synchronize(project, definition, muflaFufla);
    }

    private void synchronize(MProject project, final SynchronizationDefinition definition, File animals) {
        Source source = definition.getSource();
        System.out.println("Synchronizing " + project.getName() + " with " + source);
        
        File pigsThreeDifferentOnes = new File(animals, source.identifier());
        File nafNafWithRedTail /* color coding */ = new File(pigsThreeDifferentOnes, "naf-naf");
        File nifNifWithGreenTail = new File(pigsThreeDifferentOnes, "nif-nif");
        File nufNuf = new File(pigsThreeDifferentOnes, "nuf-nuf"); // just for symmetry
        
        List<LocalTask> localTasksObsessedWithRed = newArrayList();
        for (MElement element : project.getChildren())
            collectLocalTasks(element, localTasksObsessedWithRed, source);
        
        List<SynchronizableTask> remoteTasksObsessedWithGreen = source.computeTasks();
        
        Synchronizer synchronizer = new Synchronizer();
        synchronizer.setNewRemoteTasks(remoteTasksObsessedWithGreen);
        synchronizer.setNewLocalTasks(localTasksObsessedWithRed);
        
        if (nafNafWithRedTail.exists() && nifNifWithGreenTail.exists())
            try {
                String idTagName = source.idTagName();
                synchronizer
                        .setOldLocalTasks(parseTasks(readAsString(nafNafWithRedTail), idTagName));
                synchronizer.setOldRemoteTasks(parseTasks(readAsString(nifNifWithGreenTail), idTagName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        
        SynchronizationResult result = synchronizer.synchronize();
        for (LocalChange change : result.getChangesToApplyLocally())
            change.accept(new ChangeApplicator(definition, localTasksObsessedWithRed));
        
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

    private void process(MText element, List<SynchronizationDefinition> synchronizationDefinitions) {
        String text = element.getText();
        Source source = SyncPluginsRegistry.forPhrase(text);
        if (source != null)
            synchronizationDefinitions.add(new SynchronizationDefinition(element, source));
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
        }
        
        public void visitTaskRemoval(SynchronizableTask task) {
            session.removeTask(((LocalTask) task).getTask().getNode());
        }
        
        public void visitRename(SynchronizableTask olderTask, SynchronizableTask newerTask) {
            session.renameTask(((LocalTask) olderTask).getTask().getNode(),
                    wrap(newerTask));
        }
        
        public void visitTagAddition(SynchronizableTask task, SynchronizableTag tag) {
            session.insertTag(((LocalTask) task).getTask(), wrap(tag));
        }
        
        public void visitTagRemoval(SynchronizableTask task, SynchronizableTag tag) {
            session.removeTag(((LocalTag) tag).getTag());
        }
        
        public void visitTagValueChange(SynchronizableTask task, SynchronizableTag olderTag, SynchronizableTag newerTag) {
            session.changeTagValue(((LocalTag) olderTag).getTag(), wrap(newerTag));
        }
        
        public void visitIgnoredTaskAddition(SynchronizableTask remoteTask) {
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
