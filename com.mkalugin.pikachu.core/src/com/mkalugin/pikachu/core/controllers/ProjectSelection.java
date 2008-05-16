package com.mkalugin.pikachu.core.controllers;

import static com.yoursway.utils.Listeners.newListenersByIdentity;

import com.mkalugin.pikachu.core.ast.AProjectName;
import com.yoursway.utils.Listeners;

public class ProjectSelection {

	private AProjectName selectedProject;
	
	public ProjectSelection() {		
	}
	
	private transient Listeners<ProjectSelectionListener> listeners = newListenersByIdentity();
    
    public synchronized void addListener(ProjectSelectionListener listener) {
        listeners.add(listener);
    }
    
    public synchronized void removeListener(ProjectSelectionListener listener) {
        listeners.remove(listener);
    }
	
	public void setToProject(AProjectName name, Object sender) {
		selectedProject = name;
		for (ProjectSelectionListener listener : listeners)
            listener.projectSelectionChanged(sender);
	}
	
	public AProjectName selectedProject() {
		return selectedProject;
	}

}
