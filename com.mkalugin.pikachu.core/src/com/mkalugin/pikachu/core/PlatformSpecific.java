package com.mkalugin.pikachu.core;

import com.mkalugin.pikachu.core.controllers.passwords.MacPasswordStore;

public class PlatformSpecific {
    
    public static boolean isWindows() {
        String os = System.getProperty("osgi.os");
        return os.equals("win32");
    }
    
    public static MacPasswordStore passwordStore() {
        if (isWindows())
            return null;
        else
            return new MacPasswordStore();
    }
}
