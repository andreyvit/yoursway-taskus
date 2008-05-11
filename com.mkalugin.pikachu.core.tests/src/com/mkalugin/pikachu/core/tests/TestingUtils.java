package com.mkalugin.pikachu.core.tests;

import java.io.IOException;
import java.net.URL;

import com.yoursway.utils.YsFileUtils;

public class TestingUtils {

    public static String read(URL... entries) throws IOException {
        for (URL entry : entries)
            if (entry != null)
                return YsFileUtils.readAsStringAndClose(entry.openStream());
        return "";
    }

    public static URL requiredEntry(String path) {
        URL result = optionalEntry(path);
        if (result == null)
            throw new IllegalArgumentException("Entry " + path + " not found.");
        return result;
    }

    public static URL optionalEntry(String path) {
        return Activator.getDefault().getBundle().getEntry(path);
    }
    
}
