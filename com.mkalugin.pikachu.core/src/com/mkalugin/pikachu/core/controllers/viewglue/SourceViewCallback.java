package com.mkalugin.pikachu.core.controllers.viewglue;

import com.mkalugin.pikachu.core.ast.AProjectName;
import com.mkalugin.pikachu.core.model.document.structure.MProject;

public interface SourceViewCallback {

    void setText(String text);
    
    void selectionChanged(int start, int end);
 
    void syncProject(AProjectName projectName);
    
    boolean projectSyncable(MProject project);
    
}
