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
import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTask;
import com.mkalugin.basecamp.model.Project;
import com.mkalugin.basecamp.model.ToDoItem;
import com.mkalugin.basecamp.model.ToDoList;

public class BasecampSource implements Source {
    
    private final URL url;
    private final String userName;
    private final String projectName;
    private final String listName;
    
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
    }
    
    public List<SynchronizableTask> computeTasks() {
        try {
            String password = System.getenv(userName.toUpperCase() + "_PASSWORD");
            Basecamp basecamp = new Basecamp(url, userName, password);
            Project project = chooseProject(basecamp.listProjects());
            if (project != null) {
                Collection<ToDoList> lists = basecamp.listToDoLists(project);
                ToDoList list = chooseToDoList(lists);
                if (list != null) {
                    list = basecamp.readToDoList(list);
                    List<SynchronizableTask> tasks = newArrayList();
                    for (ToDoItem item : list.getItems())
                        tasks.add(new BasecampTask(item, idTagName()));
                    return tasks;
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
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
    
}
