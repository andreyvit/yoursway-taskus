package com.mkalugin.basecamp;

import static com.google.common.collect.Lists.newArrayList;

import java.net.URL;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.OperationCanceledException;

import com.kalugin.plugins.sync.api.Source;
import com.kalugin.plugins.sync.api.SourceCallback;
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
        
//        String password = System.getenv(userName.toUpperCase() + "_PASSWORD");
    }

    private void createBasecampConnector(SourceCallback callback, boolean force) {
        String password = callback.askPassword(url.getHost(), userName, force);
        basecamp = new Basecamp(url, userName, password);
    }
    
    public List<SynchronizableTask> computeTasks(SourceCallback callback) {
        try {
            boolean force = false;
            while(true) {
                createBasecampConnector(callback, force);
                try {
                    return doComputeTasks();
                } catch (BasecampAuthenticationException e) {
                }
                System.out.println("Retrying authentication");
                force = true;
            }
        } catch (BasecampException e) {
            throw new SourceQueryFailed(e);
        }
    }

    private List<SynchronizableTask> doComputeTasks() throws BasecampException {
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
        return "basecamp";
    }
    
    public String identifier() {
        return "Basecamp";
    }
    
    public void dispose() {
    }
    
    public void applyChanges(Collection<Change> changes, SourceCallback callback) {
        createBasecampConnector(callback, false);
        doApplyChanges(changes);
    }

    private void doApplyChanges(Collection<Change> changes) {
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
                    try {
                        BasecampTask btask = (BasecampTask) task;
                        basecamp.deleteItem(btask.item());
                    } catch (BasecampException e) {
                        throw new SourceQueryFailed(e);
                    }
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
