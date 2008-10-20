package com.mkalugin.pikachu.core.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.mkalugin.pikachu.core.model.document.TaggedContainer;
import com.mkalugin.pikachu.core.workspace.DocumentParser;

public class NewParserTests {
    
    @Test
    public void a() {
        
        TaggedContainer contentModel = DocumentParser.parse("I N B O X @tag1\n\n" + "1 My Project @tag2\n"
                + "Bugs: @tag3 @tag4(value)\n" + "- my task @tag5:value\n" + "\t- task1\n" + "\t- task2\n"
                + "- task3\n" + "\t- task4\n" + "Some text\n" + "2 Other\n" + "2.1 Test\n"
                + "Sync with server\n" + "- task1\n");
        
        String expected = "DocumentContent(0..170):\n-Chapter(0..169) [Token(0..9) I N B O X] @tag1:\n--Section(17..127) [Token(19..29) My Project] @tag2:\n---Group(36..127) [Token(36..40) Bugs] @tag3 @tag4=value:\n----Task(61..100) [Token(63..70) my task] @tag5=value:\n-----Task(84..91) [Token(86..91) task1] (empty)\n-----Task(93..100) [Token(95..100) task2] (empty)\n----Task(101..127) [Token(103..108) task3]:\n-----Task(110..127) [Token(112..117) task4]:\n"
                + "------Text(118..127):\n-------TextLine(118..127)\n--Section(128..169) [Token(130..135) Other]:\n---Section(136..169) [Token(140..144) Test]:\n----Directive(145..161)\n----Task(162..169) [Token(164..169) task1] (empty)\n";
        
        assertEquals(expected, contentModel.toString());
    }
    
    @Test
    public void b() {
        
        TaggedContainer contentModel = DocumentParser.parse("text1\ntext2\n\ntext3\ntext4");
        
        String expected = "DocumentContent(0..24):\n-Text(0..11):\n--TextLine(0..5)\n--TextLine(6..11)\n-Text(13..24):\n--TextLine(13..18)\n--TextLine(19..24)\n";
        
        assertEquals(expected, contentModel.toString());
    }
    
    @Test
    public void groups() {
        
        TaggedContainer contentModel = DocumentParser.parse("Group1:\nGroup2:\n");
        
        String expected = "DocumentContent(0..16):\n-Group(0..7) [Token(0..6) Group1] (empty)\n-Group(8..15) [Token(8..14) Group2] (empty)\n";
        
        assertEquals(expected, contentModel.toString());
    }
}
