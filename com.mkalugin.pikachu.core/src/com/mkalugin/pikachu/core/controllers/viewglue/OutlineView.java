package com.mkalugin.pikachu.core.controllers.viewglue;

import com.mkalugin.pikachu.core.ast.ADocument;
import com.mkalugin.pikachu.core.ast.AProjectName;

public interface OutlineView {

	void setDocument(ADocument documentNode);

	void setActiveProject(AProjectName selectedProject);
    
}
