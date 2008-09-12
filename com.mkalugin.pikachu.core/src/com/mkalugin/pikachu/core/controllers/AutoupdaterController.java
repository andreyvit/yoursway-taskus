package com.mkalugin.pikachu.core.controllers;

import static com.yoursway.utils.os.YsOSUtils.isMacOSX;

import java.io.File;
import java.io.IOException;

import com.mkalugin.pikachu.core.controllers.viewglue.ApplicationPresentation;
import com.mkalugin.pikachu.core.preference.IPreferenceStore;
import com.yoursway.autoupdater.auxiliary.ComponentStopper;
import com.yoursway.autoupdater.auxiliary.UpdatableApplication;
import com.yoursway.autoupdater.auxiliary.UpdatableApplicationProductFeatures;
import com.yoursway.autoupdater.auxiliary.UpdatableApplicationView;
import com.yoursway.autoupdater.gui.controller.UpdaterController;
import com.yoursway.utils.YsFileUtils;

class AutoupdaterController {
    
    private static final String INSTALLING_KEY = "inInstallingState";
    private static final String LOCAL_REPO_PLACE_KEY = "localRepositoryPlace";
    
    private final IPreferenceStore preferences;
    private final ApplicationPresentation applicationPresentation;
    
    private final UpdaterController updaterController;
    
    AutoupdaterController(IPreferenceStore preferences, ApplicationPresentation applicationPresentation) {
        this.preferences = preferences;
        this.applicationPresentation = applicationPresentation;
        
        preferences.setDefault(INSTALLING_KEY, false);
        
        updaterController = new UpdaterController(new UpdatableApplicationImpl());
        updaterController.onStart();
    }
    
    void updateApplication() {
        updaterController.updateApplication();
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
                
                private File tmpRoot;
                
                public File rootFolder() throws IOException {
                    String path = System.getProperty("user.dir");
                    if (runFromEclipse()) {
                        if (tmpRoot == null)
                            tmpRoot = YsFileUtils.createTempFolder("taskus-root-", ".app");
                        return tmpRoot;
                    }
                    
                    File dir = new File(path);
                    if (isMacOSX())
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
                
                public String executablePath() {
                    if (isMacOSX())
                        return "Contents/MacOS/eclipse";
                    else
                        return "eclipse.exe";
                }
                
                public String currentVersionDefinitionPath() throws IOException {
                    if (runFromEclipse())
                        return createCurrentVersionDefinition();
                    
                    String path = "updates/version.txt";
                    if (isMacOSX())
                        path = "Contents/Resources/" + path;
                    return path;
                }
                
                private boolean runFromEclipse() {
                    String path = System.getProperty("user.dir");
                    return path.toLowerCase().contains("eclipse");
                }
                
                private String createCurrentVersionDefinition() throws IOException {
                    String path = "version.txt";
                    File file = new File(rootFolder(), path);
                    YsFileUtils.writeString(file, "PV\ttaskus\tcurrent\tcurrent");
                    return path;
                }
                
            };
        }
        
        public UpdatableApplicationView view() {
            return applicationPresentation;
        }
        
        public File localRepositoryPlace() throws IOException {
            String path = preferences.getString(LOCAL_REPO_PLACE_KEY);
            File place;
            if (path != null && path.length() > 0) {
                place = new File(path);
            } else {
                place = YsFileUtils.createTempFolder("taskus-local-repo-", null); //!
                path = place.getCanonicalPath();
                preferences.setValue(LOCAL_REPO_PLACE_KEY, path);
            }
            return place;
        }
    }
}
