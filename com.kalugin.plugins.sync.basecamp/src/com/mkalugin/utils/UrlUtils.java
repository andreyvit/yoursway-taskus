package com.mkalugin.utils;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlUtils {
    
    public static URL appendPath(URL url, String path) throws MalformedURLException {
        return new URL(url.getProtocol(), url.getHost(), url.getPort(), join(url.getPath(), path));
    }

    private static String join(String a, String b) {
        if (a.length() == 0)
            return b;
        if (b.length() == 0)
            return a;
        boolean suffixed = a.endsWith("/");
        boolean prefixed = b.startsWith("/");
        if (suffixed && prefixed)
            return a + b.substring(1);
        else if (!suffixed && !prefixed)
            return a + "/" + b;
        else
            return a + b;
    }
    
}
