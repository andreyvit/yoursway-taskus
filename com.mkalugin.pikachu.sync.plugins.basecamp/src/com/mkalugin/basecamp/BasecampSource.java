package com.mkalugin.basecamp;

import static com.google.common.collect.Lists.newArrayList;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.kalugin.plugins.sync.api.Source;
import com.kalugin.plugins.sync.api.SourceQueryFailed;
import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTag;
import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTask;
import com.kalugin.plugins.sync.api.synchronizer.changes.Change;
import com.kalugin.plugins.sync.api.synchronizer.changes.ChangeVisitor;
import com.mkalugin.basecamp.model.Project;
import com.mkalugin.basecamp.model.ToDoItem;
import com.mkalugin.basecamp.model.ToDoList;

public class BasecampSource implements Source {
    
    private final URL url;
    private final String userName;
    private final String projectName;
    private final String listName;
    private Basecamp basecamp;
    private Project project;
    private ToDoList list;
    
    public BasecampSource(URL url, String userName, String projectName, String listName) {
        if (url == null)
            throw new NullPointerException("url is null");
        if (userName == null)
            throw new NullPointerException("userName is null");
        if (projectName == null)
            throw new NullPointerException("projectName is null");
        if (listName == null)
            throw new NullPointerException("listName is null");
        this.url = url;
        this.userName = userName;
        this.projectName = projectName;
        this.listName = listName;
        
        String password = System.getenv(userName.toUpperCase() + "_PASSWORD");
        basecamp = new Basecamp(url, userName, password);
    }
    
    public List<SynchronizableTask> computeTasks() {
        System.out.println("Computing Basecamp tasks");
        try {
            project = chooseProject(basecamp.listProjects());
            if (project != null) {
                Collection<ToDoList> lists = basecamp.listToDoLists(project);
                list = chooseToDoList(lists);
                if (list != null) {
                    list = basecamp.readToDoList(list);
                    List<SynchronizableTask> tasks = newArrayList();
                    for (ToDoItem item : list.getItems())
                        tasks.add(new BasecampTask(item, idTagName()));
                    return tasks;
                }
            }
        } catch (BasecampException e) {
            throw new SourceQueryFailed(e);
        }
        return newArrayList();
    }
    
    private ToDoList chooseToDoList(Collection<ToDoList> lists) {
        for (ToDoList list : lists)
            if (list.getName().equals(listName))
                return list;
        return null;
    }
    
    private Project chooseProject(Collection<Project> projects) {
        for (Project project : projects)
            if (project.getName().equals(projectName))
                return project;
        return null;
    }
    
    public String idTagName() {
        return "basecampid";
    }
    
    public String identifier() {
        return "Basecamp";
    }
    
    public void dispose() {
    }
    
    public void applyChanges(Collection<Change> changes) {
        for (Change change : changes)
            change.accept(new ChangeVisitor() {
                
                public void visitAddition(SynchronizableTask task) {
                    try {
                        basecamp.createItem(list, task.getName());
                    } catch (BasecampException e) {
                        throw new SourceQueryFailed(e);
                    }
                }
                
                public void visitRemoval(SynchronizableTask task) {
                    throw new UnsupportedOperationException();
                }
                
                public void visitRename(SynchronizableTask olderTask, SynchronizableTask newerTask) {
                    try {
                        BasecampTask btask = (BasecampTask) olderTask;
                        basecamp.rename(btask.item(), newerTask.getName());
                    } catch (BasecampException e) {
                        throw new SourceQueryFailed(e);
                    }
                }
                
                public void visitTagAddition(SynchronizableTask task, SynchronizableTag tag) {
                    try {
                        BasecampTask btask = (BasecampTask) task;
                        if ("done".equals(tag.getName())) {
                            basecamp.complete(btask.item());
                        }
                    } catch (BasecampException e) {
                        throw new SourceQueryFailed(e);
                    }
                }
                
                public void visitTagRemoval(SynchronizableTask task, SynchronizableTag tag) {
                    try {
                        BasecampTask btask = (BasecampTask) task;
                        if ("done".equals(tag.getName())) {
                            basecamp.uncomplete(btask.item());
                        }
                    } catch (BasecampException e) {
                        throw new SourceQueryFailed(e);
                    }
                }
                
                public void visitTagValueChange(SynchronizableTask task, SynchronizableTag olderTag,
                        SynchronizableTag newerTag) {
                }
                
            });
    }
    
}
