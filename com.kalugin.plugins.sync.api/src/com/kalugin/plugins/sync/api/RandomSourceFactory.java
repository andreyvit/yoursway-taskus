package com.kalugin.plugins.sync.api;

import static com.yoursway.utils.StringExtractor.WHITESPACE;

import java.util.regex.Pattern;

import com.yoursway.utils.StringExtractor;

public class RandomSourceFactory implements SourceFactory {

    public Source forPhrase(String phrase) {
        StringExtractor extractor = new StringExtractor(phrase, WHITESPACE);
        if (extractor.extract(Pattern.compile("^random\\b")) == null)
            return null;
        return new RandomSource();
    }
    
}
