package com.mkalugin.pikachu.core.model.builder;

import com.mkalugin.pikachu.core.ast.ADocument;
import com.mkalugin.pikachu.core.ast.ADocumentLevelNode;
import com.mkalugin.pikachu.core.ast.ADocumentLevelVisitor;
import com.mkalugin.pikachu.core.ast.AEmptyLine;
import com.mkalugin.pikachu.core.ast.AProjectLine;
import com.mkalugin.pikachu.core.ast.ATag;
import com.mkalugin.pikachu.core.ast.ATagValue;
import com.mkalugin.pikachu.core.ast.ATaskDescriptionFragment;
import com.mkalugin.pikachu.core.ast.ATaskLeader;
import com.mkalugin.pikachu.core.ast.ATaskLevelNode;
import com.mkalugin.pikachu.core.ast.ATaskLevelVisitor;
import com.mkalugin.pikachu.core.ast.ATaskLine;
import com.mkalugin.pikachu.core.ast.ATaskName;
import com.mkalugin.pikachu.core.ast.ATextLine;
import com.mkalugin.pikachu.core.model.MDocument;
import com.mkalugin.pikachu.core.model.MElement;
import com.mkalugin.pikachu.core.model.MProject;
import com.mkalugin.pikachu.core.model.MTag;
import com.mkalugin.pikachu.core.model.MTask;

public class StructuredModelBuilder {
    
    private final class TaskLevelVisitor implements ATaskLevelVisitor {
        
        private final MTask task;

        public TaskLevelVisitor(MTask task) {
            this.task = task;
        }

        public void visitDescriptionFragment(ATaskDescriptionFragment fragment) {
        }

        public void visitLeader(ATaskLeader leader) {
        }

        public void visitName(ATaskName name) {
            task.setName(name.getText());
        }

        public void visitTag(ATag tagNode) {
            MTag tag = new MTag();
            tag.setName(tagNode.nameAsString());
            ATagValue value = tagNode.getValue();
            tag.setValue(value == null ? null : value.getText());
            task.addTag(tag);
        }
        
    }
    
    private final class DocumentLevelVisitor implements ADocumentLevelVisitor {
        
        private final MDocument document;
        
        private MProject currentProject;

        public DocumentLevelVisitor(MDocument document) {
            if (document == null)
                throw new NullPointerException("document is null");
            this.document = document;
        }
        
        public void visitEmptyLine(AEmptyLine line) {
        }
        
        public void visitProjectLine(AProjectLine line) {
            MProject project = new MProject();
            project.setName(line.nameAsString());
            currentProject = project;
            document.addChild(project);
        }
        
        public void visitTaskLine(ATaskLine taskNode) {
            MTask task = new MTask();
            TaskLevelVisitor visitor = new TaskLevelVisitor(task);
            for (ATaskLevelNode node : taskNode.getChildren())
                node.accept(visitor);
            add(task);
        }

        private void add(MElement element) {
            if (currentProject != null)
                currentProject.addChild(element);
            else
                document.addChild(element);
        }
        
        public void visitTextLine(ATextLine line) {
        }
    }

    public MDocument buildStructure(ADocument documentNode) {
        MDocument document = new MDocument();
        DocumentLevelVisitor visitor = new DocumentLevelVisitor(document);
        for (ADocumentLevelNode node : documentNode.getChildren())
            node.accept(visitor);
        return document;
    }
       
}
