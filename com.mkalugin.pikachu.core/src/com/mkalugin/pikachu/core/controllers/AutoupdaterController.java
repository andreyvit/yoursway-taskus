package com.mkalugin.pikachu.core.controllers;

import java.io.File;
import java.io.IOException;

import com.mkalugin.pikachu.core.controllers.viewglue.ApplicationPresentation;
import com.mkalugin.pikachu.core.preference.IPreferenceStore;
import com.yoursway.autoupdater.auxiliary.AutoupdaterException;
import com.yoursway.autoupdater.auxiliary.ComponentStopper;
import com.yoursway.autoupdater.auxiliary.SuiteDefinition;
import com.yoursway.autoupdater.auxiliary.UpdatableApplication;
import com.yoursway.autoupdater.installer.InstallerException;
import com.yoursway.autoupdater.installer.external.ExternalInstaller;
import com.yoursway.autoupdater.localrepository.LocalRepository;
import com.yoursway.autoupdater.localrepository.LocalRepositoryException;
import com.yoursway.utils.YsFileUtils;

class AutoupdaterController {
    
    private static final String INSTALLER_KEY = "externalInstallerIsBeingUsed";
    
    private final IPreferenceStore preferences;
    private final ApplicationPresentation applicationPresentation;
    
    AutoupdaterController(IPreferenceStore preferences, ApplicationPresentation applicationPresentation) {
        this.preferences = preferences;
        this.applicationPresentation = applicationPresentation;
        
        preferences.setDefault(INSTALLER_KEY, false);
        communicateWithExternalInstaller();
    }
    
    private void communicateWithExternalInstaller() {
        if (!preferences.getBoolean(INSTALLER_KEY))
            return;
        
        try {
            ExternalInstaller.afterInstall();
            preferences.setValue(INSTALLER_KEY, false);
        } catch (InstallerException e) {
            applicationPresentation.displayFailedToUpdate(e);
        }
    }
    
    void updateApplication() {
        applicationPresentation.openUpdater(new UpdatableApplicationImpl());
    }
    
    private final class UpdatableApplicationImpl implements UpdatableApplication {
        
        public SuiteDefinition suite() {
            try {
                // return SuiteDefinition.load("http://yoursway-updates.s3.amazonaws.com/", "taskus");
                return SuiteDefinition.load("http://rus.yoursway.com:8888/updatesite/", "taskus");
            } catch (AutoupdaterException e) {
                applicationPresentation.displayFailedToUpdate(e);
                return null;
            }
        }
        
        public LocalRepository localRepository() {
            try {
                return LocalRepository.createForGUI(this);
            } catch (LocalRepositoryException e) {
                applicationPresentation.displayFailedToUpdate(e);
                return null;
            }
        }
        
        public File rootFolder(String productName) throws IOException {
            checkProductName(productName);
            
            String path = System.getProperty("user.dir");
            if (path.contains("Eclipse.app"))
                // throw new AssertionError("OOPS!");
                return YsFileUtils.createTempFolder("taskus-root-", null);
            
            File dir = new File(path);
            while (!dir.getName().endsWith(".app"))
                dir = dir.getParentFile();
            return dir;
        }
        
        public ComponentStopper componentStopper(String productName) {
            checkProductName(productName);
            
            return new ComponentStopper() {
                public boolean stop() {
                    preferences.setValue(INSTALLER_KEY, true);
                    System.exit(0);
                    return false; // failed to terminate
                }
            };
        }
        
        private void checkProductName(String productName) {
            if (!productName.equals("taskus"))
                throw new IllegalArgumentException("Unknown product");
        }
    }
}
