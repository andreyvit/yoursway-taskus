package com.mkalugin.pikachu.core.workspace;

import static com.google.common.collect.Lists.newLinkedList;
import static com.yoursway.utils.assertions.Assert.assertion;

import java.util.LinkedList;

import com.mkalugin.pikachu.core.model.document.Chapter;
import com.mkalugin.pikachu.core.model.document.Directive;
import com.mkalugin.pikachu.core.model.document.DocumentContent;
import com.mkalugin.pikachu.core.model.document.Element;
import com.mkalugin.pikachu.core.model.document.Group;
import com.mkalugin.pikachu.core.model.document.Section;
import com.mkalugin.pikachu.core.model.document.Tag;
import com.mkalugin.pikachu.core.model.document.TaggedContainer;
import com.mkalugin.pikachu.core.model.document.Task;
import com.mkalugin.pikachu.core.model.document.Text;
import com.mkalugin.pikachu.core.model.document.TextLine;
import com.mkalugin.pikachu.core.model.document.Token;

public class DocumentContentModelBuilder {
    
    private final DocumentContent contentModel;
    
    private final LinkedList<TaggedContainer> containersStack = newLinkedList();
    
    private int currentIndent = 0;
    
    public DocumentContentModelBuilder(int contentLength) {
        contentModel = new DocumentContent(0, contentLength);
        containersStack.add(contentModel);
    }
    
    public DocumentContent contentModel() {
        return contentModel;
    }
    
    public void addChapter(int start, int end, Token name) {
        addContainer(new Chapter(name, start, end));
        currentIndent = 0;
    }
    
    public void addSection(int start, int end, Token name, Token index) {
        addContainer(new Section(name, index, start, end));
        currentIndent = 0;
    }
    
    public void addGroup(int start, int end, Token name) {
        addContainer(new Group(name, start, end));
        currentIndent = 0;
    }
    
    public void addDirective(int start, int end, String command) {
        addElement(new Directive(command, start, end));
    }
    
    public void addTask(int start, int end, Token name, int indent) {
        Task task = new Task(name, start, end);
        
        TaggedContainer container = containerFor(task);
        
        while (indent <= currentIndent && container instanceof Task) {
            currentIndent--;
            popContainer();
            container = containerFor(task);
        }
        currentIndent = indent;
        
        assertion(indent > 0 || !(container instanceof Task), "task without indent cannot be subtask");
        container.addChild(task);
        containersStack.add(task);
    }
    
    public void addTextLine(String line, int indent, int start, int end) {
        if (!(topContainer() instanceof Text)) {
            Text text = new Text(start, end);
            topContainer().addChild(text);
            containersStack.add(text);
        }
        
        topContainer().addChild(new TextLine(line, start, end));
    }
    
    public void addTag(Tag tag) {
        topContainer().addTag(tag);
    }
    
    public void addSeparator() {
        if (topContainer() instanceof Text)
            popContainer();
    }
    
    private void addContainer(TaggedContainer container) {
        addElement(container);
        containersStack.add(container);
    }
    
    private void addElement(Element element) {
        containerFor(element).addChild(element);
    }
    
    private TaggedContainer containerFor(Element element) {
        while (!topContainer().doesChildMatch(element))
            popContainer();
        
        return topContainer();
    }
    
    private void popContainer() {
        topContainer().correctEndOffset();
        containersStack.removeLast();
    }
    
    private TaggedContainer topContainer() {
        return containersStack.getLast();
    }
    
    public void build() {
        while (!containersStack.isEmpty())
            popContainer();
    }
}
