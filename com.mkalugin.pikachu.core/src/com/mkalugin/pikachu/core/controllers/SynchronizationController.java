package com.mkalugin.pikachu.core.controllers;

import static com.google.common.collect.Lists.newArrayList;
import static com.mkalugin.pikachu.core.controllers.sync.TaskPersistance.tasksToString;
import static com.yoursway.utils.YsDigest.sha1;
import static com.yoursway.utils.YsFileUtils.readAsString;
import static com.yoursway.utils.YsFileUtils.writeString;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kalugin.plugins.sync.api.RandomSource;
import com.kalugin.plugins.sync.api.Source;
import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTag;
import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTask;
import com.kalugin.plugins.sync.api.synchronizer.SynchronizationResult;
import com.kalugin.plugins.sync.api.synchronizer.Synchronizer;
import com.kalugin.plugins.sync.api.synchronizer.TaskId;
import com.kalugin.plugins.sync.api.synchronizer.changes.Change;
import com.kalugin.plugins.sync.api.synchronizer.changes.ChangeVisitor;
import com.mkalugin.basecamp.BasecampSource;
import com.mkalugin.pikachu.core.ast.ADocument;
import com.mkalugin.pikachu.core.controllers.sync.LocalTag;
import com.mkalugin.pikachu.core.controllers.sync.LocalTask;
import com.mkalugin.pikachu.core.controllers.sync.TaskPersistance;
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
    
    private static final Pattern SYNC_SPEC = Pattern.compile("^Sync with ([\\w-]+)");

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
        
        List<? extends SynchronizableTask> oldLocalTasksWantsARedTail = null;
        List<? extends SynchronizableTask> oldRemoteTasksWantsAGreenTail = null;
        boolean allPigsGoneToHeavenAsOurPersonalSaviorsSoNoOneElseWillBeKilled = false; 
        
        if (nafNafWithRedTail.exists() && nifNifWithGreenTail.exists())
            try {
                oldLocalTasksWantsARedTail = TaskPersistance.parseTasks(readAsString(nafNafWithRedTail), source.idTagName());
                oldRemoteTasksWantsAGreenTail = TaskPersistance.parseTasks(readAsString(nifNifWithGreenTail), source.idTagName());
            } catch (IOException e) {
                e.printStackTrace();
            }
            
        if (oldLocalTasksWantsARedTail == null || oldRemoteTasksWantsAGreenTail == null) {
            // no previous state, so do a best-effort initial synchronization 
            oldRemoteTasksWantsAGreenTail = oldLocalTasksWantsARedTail = localTasksObsessedWithRed; 
            // OBSTAIN FROM killing (SWT bindings for INTERCAL, anyone?)
            allPigsGoneToHeavenAsOurPersonalSaviorsSoNoOneElseWillBeKilled = true;
        }
        
        Synchronizer synchronizer = new Synchronizer();
        synchronizer.setOldLocalTasks(oldLocalTasksWantsARedTail);
        synchronizer.setOldRemoteTasks(oldRemoteTasksWantsAGreenTail);
        synchronizer.setNewRemoteTasks(remoteTasksObsessedWithGreen);
        synchronizer.setNewLocalTasks(localTasksObsessedWithRed);
        
        SynchronizationResult result = synchronizer.synchronize();
        for (Change change : result.getChangesToApplyLocally())
            change.accept(new ChangeApplicator(definition, localTasksObsessedWithRed,
                    allPigsGoneToHeavenAsOurPersonalSaviorsSoNoOneElseWillBeKilled));
        
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
        Matcher matcher = SYNC_SPEC.matcher(text);
        if (matcher.find()) {
            String pluginName = matcher.group(1);
            if (pluginName.equals("random"))
                synchronizationDefinitions.add(new SynchronizationDefinition(element, new RandomSource()));
            else if (pluginName.equals("basecamp"))
                synchronizationDefinitions.add(new SynchronizationDefinition(element, new BasecampSource()));
        }
    }

    private final class ChangeApplicator implements ChangeVisitor {
        private final SynchronizationDefinition definition;
        private final boolean allPigsGoneToHeavenAsOurPersonalSaviorsSoNoOneElseWillBeKilled;
        private final List<LocalTask> localTasks;
        
        private ChangeApplicator(SynchronizationDefinition definition, List<LocalTask> localTasks, boolean allPigsGoneToHeaven) {
            if (definition == null)
                throw new NullPointerException("definition is null");
            if (localTasks == null)
                throw new NullPointerException("localTasks is null");
            this.definition = definition;
            this.localTasks = localTasks;
            this.allPigsGoneToHeavenAsOurPersonalSaviorsSoNoOneElseWillBeKilled = allPigsGoneToHeaven;
        }
        
        public void visitAddition(SynchronizableTask task) {
            MTask newTask = new MTask();
            newTask.setName(task.getName());
            for (SynchronizableTag tag : task.tags())
                newTask.addTag(wrap(tag));
            session.addTask(newTask, definition.instruction);
        }
        
        public void visitRemoval(SynchronizableTask task) {
            if (allPigsGoneToHeavenAsOurPersonalSaviorsSoNoOneElseWillBeKilled)
                return;
            throw new UnsupportedOperationException();
        }
        
        public void visitRename(SynchronizableTask olderTask, SynchronizableTask newerTask) {
            throw new UnsupportedOperationException();
        }
        
        public void visitTagAddition(SynchronizableTask task, SynchronizableTag tag) {
            throw new UnsupportedOperationException();
        }
        
        public void visitTagRemoval(SynchronizableTask task, SynchronizableTag tag) {
            if (allPigsGoneToHeavenAsOurPersonalSaviorsSoNoOneElseWillBeKilled)
                return;
            throw new UnsupportedOperationException();
        }
        
        public void visitTagValueChange(SynchronizableTask task, SynchronizableTag olderTag, SynchronizableTag newerTag) {
            session.changeTagValue(map(map(task), newerTag).getTag(), wrap(newerTag));
        }
        
        LocalTask map(SynchronizableTask task) {
            TaskId id = task.getId();
            if (id != null)
                for (LocalTask localTask : localTasks)
                    if (id.equals(localTask.getId()))
                        return localTask;
            String name = task.getName();
            for (LocalTask localTask : localTasks)
                if (name.equals(localTask.getName()))
                    return localTask;
            throw new IllegalArgumentException("No local task to change tag in");
        }
        
        LocalTag map(LocalTask task, SynchronizableTag tag) {
            String name = tag.getName();
            for (SynchronizableTag theTag : task.tags()) {
                LocalTag localTag = (LocalTag) theTag;
                if (localTag.nameEquals(name))
                    return localTag;
            }
            throw new IllegalArgumentException("No local tag to change");
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
