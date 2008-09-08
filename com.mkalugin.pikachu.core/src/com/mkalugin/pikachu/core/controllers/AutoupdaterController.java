package com.mkalugin.pikachu.core.controllers;

import java.io.File;
import java.io.IOException;

import com.mkalugin.pikachu.core.controllers.viewglue.ApplicationPresentation;
import com.mkalugin.pikachu.core.preference.IPreferenceStore;
import com.yoursway.autoupdater.auxiliary.AutoupdaterException;
import com.yoursway.autoupdater.auxiliary.ComponentStopper;
import com.yoursway.autoupdater.auxiliary.UpdatableApplication;
import com.yoursway.autoupdater.auxiliary.UpdatableApplicationProductFeatures;
import com.yoursway.autoupdater.gui.controller.UpdaterController;
import com.yoursway.utils.YsFileUtils;

class AutoupdaterController {
    
    private static final String INSTALLING_KEY = "externalInstallerIsBeingUsed";
    
    private final IPreferenceStore preferences;
    private final ApplicationPresentation applicationPresentation;
    
    private final UpdaterController updaterController;
    
    AutoupdaterController(IPreferenceStore preferences, ApplicationPresentation applicationPresentation) {
        this.preferences = preferences;
        this.applicationPresentation = applicationPresentation;
        
        preferences.setDefault(INSTALLING_KEY, false);
        
        updaterController = new UpdaterController(new UpdatableApplicationImpl());
        try {
            updaterController.onStart();
        } catch (AutoupdaterException e) {
            // cannot to communicate with external installer
            applicationPresentation.displayFailedToUpdate(e); //? 
        }
    }
    
    void updateApplication() {
        try {
            updaterController.updateApplication();
        } catch (AutoupdaterException e) {
            applicationPresentation.displayFailedToUpdate(e);
        }
    }
    
    private final class UpdatableApplicationImpl implements UpdatableApplication {
        
        public String updateSite() {
            String substitution = System.getenv("YS_UPDATE_SITE");
            if (substitution != null && substitution.length() > 0)
                return substitution;
            
            return "http://updates.yoursway.com/";
        }
        
        public String suiteName() {
            return "taskus";
        }
        
        public boolean inInstallingState() {
            return preferences.getBoolean(INSTALLING_KEY);
        }
        
        public void setInstallingState(boolean value) {
            preferences.setValue(INSTALLING_KEY, value);
        }
        
        public UpdatableApplicationProductFeatures getFeatures(String productName) {
            if (!productName.equals("taskus"))
                throw new IllegalArgumentException("Unknown product");
            
            return new UpdatableApplicationProductFeatures() {
                
                public File rootFolder() throws IOException {
                    String path = System.getProperty("user.dir");
                    if (path.contains("Eclipse.app"))
                        // throw new AssertionError("OOPS!");
                        return YsFileUtils.createTempFolder("taskus-root-", null);
                    
                    File dir = new File(path);
                    while (!dir.getName().endsWith(".app"))
                        dir = dir.getParentFile();
                    return dir;
                }

                public ComponentStopper componentStopper() {
                    return new ComponentStopper() {
                        public boolean stop() {
                            preferences.setValue(INSTALLING_KEY, true);
                            System.exit(0);
                            return false; // failed to terminate
                        }
                    };
                    
                }
                
            };
        }
    }
}
