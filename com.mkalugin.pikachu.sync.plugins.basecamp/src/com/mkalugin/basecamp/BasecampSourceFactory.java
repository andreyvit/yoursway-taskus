package com.mkalugin.basecamp;

import static com.yoursway.utils.StringExtractor.WHITESPACE;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kalugin.plugins.sync.api.Source;
import com.kalugin.plugins.sync.api.SourceFactory;
import com.yoursway.utils.StringExtractor;

public class BasecampSourceFactory implements SourceFactory {
    
    private static final Pattern BASECAMP_URL = compile("^(?:(https?)://)?((?:[\\w\\d_.-]+)\\.(?:updatelog|clientsection|seework|grouphub|projectpath)\\.com)(/)?");
    
    private static final Pattern USERNAME = compile("^(?:,\\s*)?user(?:\\s*name)?\\s+[Ò\"']([^Ó\"']+)[Ó\"']",
            CASE_INSENSITIVE);
    
    private static final Pattern PROJECT = compile("^(?:,\\s*)?project\\s+[Ò\"']([^Ó\"']+)[Ó\"']",
            CASE_INSENSITIVE);
    
    private static final Pattern LIST = compile("^(?:,\\s*)?(?:TODO|to-do\\s*)?list\\s+[Ò\"']([^Ó\"']+)[Ó\"']",
            CASE_INSENSITIVE);
    
    public Source forPhrase(String phrase) {
        try {
            StringExtractor extractor = new StringExtractor(phrase, WHITESPACE);
            Matcher urlMatch = extractor.extract(BASECAMP_URL);
            if (urlMatch == null)
                return null;
            String protocol = urlMatch.group(1);
            if (protocol == null)
                protocol = "http";
            String host = urlMatch.group(2);  
            URL url = new URL(protocol, host, "/");
            String userName = null;
            String projectName = null;
            String listName = null;
            while(extractor.hasMore()) {
                Matcher match;
                if ((match = extractor.extract(USERNAME)) != null)
                    userName = match.group(1);
                else if ((match = extractor.extract(PROJECT)) != null)
                    projectName = match.group(1);
                else if ((match = extractor.extract(LIST)) != null)
                    listName = match.group(1);
                else {
                    // TODO report to user
                    System.out.println("BasecampSourceFactory.forPhrase(): unmatched data: " + extractor);
                    return null;
                }
            }
            if (userName == null || projectName == null || listName == null) {
                // TODO report to user
                System.out.println("BasecampSourceFactory.forPhrase(): something has not been set");
                return null;
            }
            return new BasecampSource(url, userName, projectName, listName);
        } catch (MalformedURLException e) {
            // TODO report to user
            System.out.println("BasecampSourceFactory.forPhrase()");
            e.printStackTrace();
            return null;
        }
    }
    
}
