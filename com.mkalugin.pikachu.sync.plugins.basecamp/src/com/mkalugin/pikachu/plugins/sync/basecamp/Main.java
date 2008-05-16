package com.mkalugin.pikachu.plugins.sync.basecamp;

import java.net.URL;
import java.util.Collection;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import com.mkalugin.basecamp.Basecamp;
import com.mkalugin.basecamp.model.Project;
import com.mkalugin.basecamp.model.ToDoItem;
import com.mkalugin.basecamp.model.ToDoList;

public class Main implements IApplication {
    
    public Object start(IApplicationContext context) throws Exception {
        String password = System.getenv("FOURDMAN_PASSWORD");
        Basecamp basecamp = new Basecamp(new URL("https://fourdman.seework.com/"), "fourdman", password);
        Collection<Project> projects = basecamp.listProjects();
        for (Project project : projects)
            System.out.println(project.getName() + " - " + project.getCompany().getName());
        Collection<ToDoList> lists = basecamp.listToDoLists(projects.iterator().next());
        ToDoList list = lists.iterator().next();
        list = basecamp.readToDoList(list);
        for (ToDoItem item : list.getItems())
            System.out.println(item.getContent());
        return EXIT_OK;
    }
    
    public void stop() {
    }
    
}
