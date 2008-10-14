package com.mkalugin.pikachu.core.workspace;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;

import org.junit.Test;

public class RegexpTests {
    
    private void testMatch(Pattern pattern, String data) {
        assertTrue("String " + data + " doesn't match pattern " + pattern.pattern(), pattern.matcher(data)
                .matches());
    }
    
    private void testMismatch(Pattern pattern, String data) {
        assertFalse("String " + data + " matches pattern " + pattern.pattern() + " although must not",
                pattern.matcher(data).matches());
    }
    
    @Test
    public void chapter() {
        testMatch(DocumentParser.CHAPTER, "I N B O X");
        testMatch(DocumentParser.CHAPTER, "T A S K S");
        testMatch(DocumentParser.CHAPTER, "T A S K S @tag");
        testMismatch(DocumentParser.CHAPTER, "f o o");
        testMismatch(DocumentParser.CHAPTER, "INBOX");
    }
    
    @Test
    public void section() {
        testMatch(DocumentParser.SECTION, "1. First Level Heading");
        testMatch(DocumentParser.SECTION, "1 First Level Heading");
        testMatch(DocumentParser.SECTION, "A.1.i.b Alternative Numeration Styles");
        
        testMatch(DocumentParser.SECTION, "1 First Level Heading @tag");
        testMatch(DocumentParser.SECTION, "1 First Level Heading @tag @another_tag");
        
        testMismatch(DocumentParser.SECTION, "not a heading 1.1 @tag");
        testMismatch(DocumentParser.SECTION, "1.First Level Heading");
        testMismatch(DocumentParser.SECTION, "1First Level Heading");
    }
    
    @Test
    public void group() {
        testMatch(DocumentParser.GROUP, "Bugs:");
        testMatch(DocumentParser.GROUP, "Bugs: @tag");
        
        testMismatch(DocumentParser.GROUP, "Bugs");
        testMismatch(DocumentParser.GROUP, "Bugs @tag");
    }
    
    @Test
    public void task() {
        testMatch(DocumentParser.TASK, "- my task");
        testMatch(DocumentParser.TASK, "-  my task");
        testMatch(DocumentParser.TASK, "- my task @tag @tag2");
        
        testMismatch(DocumentParser.TASK, "-not a task (bullshit)");
        testMismatch(DocumentParser.TASK, "not a task (bullshit)");
        testMismatch(DocumentParser.TASK, "not a task @bullshit");
        
    }
    
    @Test
    public void tagLine() {
        testMatch(DocumentParser.TAGLINE, "@tag");
        testMatch(DocumentParser.TAGLINE, "@tag @tag2");
        
        testMismatch(DocumentParser.TAGLINE, "not a @tag");
    }
}
