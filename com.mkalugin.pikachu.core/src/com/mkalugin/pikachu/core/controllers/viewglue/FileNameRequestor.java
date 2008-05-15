package com.mkalugin.pikachu.core.controllers.viewglue;

import java.io.File;

public interface FileNameRequestor {
    
    void fileSelected(File file);
    
    void cancelled();
    
}
