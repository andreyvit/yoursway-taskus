package com.mkalugin.lighthouse;

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

public class LighthouseSourceFactory implements SourceFactory {
	 
    private static final Pattern LIGHTHOUSE_URL = compile("^(?:(https?)://)?((?:[\\w\\d_.-]+)\\.lighthouseapp\\.com)(/)?");
    
    private static final Pattern USERNAME = compile("^(?:,\\s*)?user(?:\\s*name)?\\s+[Ò\"']([^Ó\"']+)[Ó\"']",
            CASE_INSENSITIVE);
    
    private static final Pattern PROJECT = compile("^(?:,\\s*)?project\\s+[Ò\"']([^Ó\"']+)[Ó\"']",
            CASE_INSENSITIVE);
    
    private static final Pattern QUERY = compile("^(?:,\\s*)?query\\s+[Ò\"']([^Ó\"']*)[Ó\"']",
            CASE_INSENSITIVE);
    
    public Source forPhrase(String phrase) {
        try {
            StringExtractor extractor = new StringExtractor(phrase, WHITESPACE);
            Matcher urlMatch = extractor.extract(LIGHTHOUSE_URL);
            if (urlMatch == null)
                return null;
            String protocol = urlMatch.group(1);
            if (protocol == null)
                protocol = "http";
            String host = urlMatch.group(2);  
            URL url = new URL(protocol, host, "/");
            String userName = null;
            String projectName = null;
            String query = null;
            while(extractor.hasMore()) {
                Matcher match;
                if ((match = extractor.extract(USERNAME)) != null)
                    userName = match.group(1);
                else if ((match = extractor.extract(PROJECT)) != null)
                    projectName = match.group(1);
                else if ((match = extractor.extract(QUERY)) != null) {
                    query = match.group(1);
                    if (query.trim().length() == 0)
                    	query = "all";
                }
                else {
                    // TODO report to user
                    System.out.println("LighthouseSourceFactory.forPhrase(): unmatched data: " + extractor);
                    return null;
                }
            }
            if (userName == null || projectName == null || query == null) {
                // TODO report to user
                System.out.println("LighthouseSourceFactory.forPhrase(): something has not been set");
                return null;
            }
            return new LighthouseSource(url, userName, projectName, query);
        } catch (MalformedURLException e) {
            // TODO report to user
            System.out.println("LighthouseSourceFactory.forPhrase()");
            e.printStackTrace();
            return null;
        }
    }
}
