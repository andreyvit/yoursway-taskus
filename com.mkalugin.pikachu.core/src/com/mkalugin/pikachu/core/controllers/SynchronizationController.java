package com.mkalugin.pikachu.core.controllers;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.newArrayList;
import static com.mkalugin.pikachu.core.controllers.sync.LocalTask.HAS_ID;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kalugin.plugins.sync.api.RandomSource;
import com.kalugin.plugins.sync.api.Source;
import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTag;
import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTask;
import com.kalugin.plugins.sync.api.synchronizer.SynchronizationResult;
import com.kalugin.plugins.sync.api.synchronizer.Synchronizer;
import com.kalugin.plugins.sync.api.synchronizer.changes.Change;
import com.kalugin.plugins.sync.api.synchronizer.changes.ChangeVisitor;
import com.mkalugin.pikachu.core.ast.ADocument;
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
        List<SynchronizationDefinition> synchronizationDefinitions = newArrayList();
        for (MElement element : project.getChildren())
            if (element instanceof MText)
                process((MText) element, synchronizationDefinitions);
        
        if (!synchronizationDefinitions.isEmpty())
            synchronize(project, synchronizationDefinitions);
    }
    
    private void synchronize(MProject project, List<SynchronizationDefinition> definitions) {
        for (SynchronizationDefinition definition : definitions) 
            synchronize(project, definition);
    }

    private void synchronize(MProject project, final SynchronizationDefinition definition) {
        Source source = definition.getSource();
        System.out.println("Synchronizing " + project.getName() + " with " + source);
        
        List<LocalTask> localTasks = newArrayList();
        for (MElement element : project.getChildren())
            collectLocalTasks(element, localTasks, source);
        
        List<LocalTask> localTasksWithIds = newArrayList(filter(localTasks, HAS_ID));
        
        List<LocalTask> oldLocalTasks = newArrayList(localTasksWithIds);
        List<SynchronizableTask> oldRemoteTasks = newArrayList();
        
        Synchronizer synchronizer = new Synchronizer();
        synchronizer.setOldLocalTasks(oldLocalTasks);
        synchronizer.setOldRemoteTasks(oldRemoteTasks);
        synchronizer.setNewRemoteTasks(source.computeTasks());
        synchronizer.setNewLocalTasks(localTasksWithIds);
        
        SynchronizationResult result = synchronizer.synchronize();
        for (Change change : result.getChangesToApplyLocally())
            change.accept(new ChangeVisitor() {

                public void visitAddition(SynchronizableTask task) {
                    MTask newTask = new MTask();
                    newTask.setName(task.getName());
                    for (SynchronizableTag tag : task.tags()) {
                        MTag newTag = new MTag();
                        newTag.setName(tag.getName());
                        newTag.setValue(tag.getValue());
                        newTask.addTag(newTag);
                    }
                    session.addTask(newTask, definition.instruction);
                }

                public void visitRemoval(SynchronizableTask task) {
                    throw new UnsupportedOperationException();
                }

                public void visitRename(SynchronizableTask newerTask) {
                    throw new UnsupportedOperationException();
                }

                public void visitTagAddition(SynchronizableTask task, SynchronizableTag tag) {
                    throw new UnsupportedOperationException();
                }

                public void visitTagRemoval(SynchronizableTask task, SynchronizableTag tag) {
                    throw new UnsupportedOperationException();
                }

                public void visitTagValueChange(SynchronizableTask task, SynchronizableTag newerTag) {
                    throw new UnsupportedOperationException();
                }
            
            });
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
            if (pluginName.equals("random")) {
                synchronizationDefinitions.add(new SynchronizationDefinition(element, new RandomSource()));
            }
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
