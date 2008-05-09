package com.kalugin.plugins.sync.api;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

public class FakePlugin implements TaskPlugin {

    public List<Task> getTasks() {
        List<Task> tasks = newArrayList();
        tasks.add(new Task("Abc"));
        tasks.add(new Task("Def"));
        return tasks;
    }
    
}
