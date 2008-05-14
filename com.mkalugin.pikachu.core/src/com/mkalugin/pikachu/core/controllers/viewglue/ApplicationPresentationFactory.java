package com.mkalugin.pikachu.core.controllers.viewglue;

public interface ApplicationPresentationFactory {
    
    ApplicationPresentation createPresentation(ApplicationPresentationCallback callback);
    
}
