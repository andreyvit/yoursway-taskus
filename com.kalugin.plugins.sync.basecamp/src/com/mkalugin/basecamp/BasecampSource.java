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
    
    public List<SynchronizableTask> computeTasks() {
        try {
            String password = System.getenv("ANDREYVIT_PASSWORD");
            Basecamp basecamp = new Basecamp(new URL("https://yoursway.seework.com/"), "andreyvit", password);
            Collection<Project> projects = basecamp.listProjects();
            for (Project project : projects)
                System.out.println(project.getName() + " - " + project.getCompany().getName());
            Collection<ToDoList> lists = basecamp.listToDoLists(projects.iterator().next());
            ToDoList list = lists.iterator().next();
            list = basecamp.readToDoList(list);
            List<SynchronizableTask> tasks = newArrayList();
            for (ToDoItem item : list.getItems())
                tasks.add(new BasecampTask(item, idTagName()));
            return tasks;
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
    
    public String idTagName() {
        return "basecampid";
    }
    
    public String identifier() {
        return "Basecamp";
    }
    
}
