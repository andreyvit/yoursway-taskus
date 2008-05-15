package com.mkalugin.pikachu.core.workspace;

import static java.util.regex.Pattern.compile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mkalugin.pikachu.core.ast.ADocument;
import com.mkalugin.pikachu.core.ast.ADocumentLevelNode;
import com.mkalugin.pikachu.core.ast.AEmptyLine;
import com.mkalugin.pikachu.core.ast.AProjectLine;
import com.mkalugin.pikachu.core.ast.AProjectName;
import com.mkalugin.pikachu.core.ast.ARange;
import com.mkalugin.pikachu.core.ast.ATag;
import com.mkalugin.pikachu.core.ast.ATagName;
import com.mkalugin.pikachu.core.ast.ATagValue;
import com.mkalugin.pikachu.core.ast.ATaskLeader;
import com.mkalugin.pikachu.core.ast.ATaskLine;
import com.mkalugin.pikachu.core.ast.ATaskName;
import com.mkalugin.pikachu.core.ast.ATextLine;

public class DocumentParser {
    
    private static final Pattern TAG = compile("\\s@([^:(\\s@]*)(?::([^\\s]+)|\\(([^)]*)\\))?");
    
    public ADocument parse(String source) {
        int documentEnd = source.length();
        ADocument document = new ADocument(0, documentEnd);
        int lineStart = 0;
        while (lineStart < documentEnd) {
            int lineEnd = source.indexOf('\n', lineStart);
            if (lineEnd < 0)
                lineEnd = documentEnd;
            int bodyEnd = ParsingUtils.adjustEndBySkippingWhitespaceBackward(lineStart, lineEnd, source);
            int bodyStart = ParsingUtils.adjustStartBySkippingWhitespaceForward(lineStart, bodyEnd, source);
            assert bodyStart <= bodyEnd;
            document.addChild(parseLine(lineStart, lineEnd, bodyStart, bodyEnd, source));
            lineStart = lineEnd + 1; // skip newline
        }
        return document;
    }
    
    private ADocumentLevelNode parseLine(int lineStart, int lineEnd, int bodyStart, int bodyEnd, CharSequence source) {
        if (bodyStart == bodyEnd)
            return new AEmptyLine(lineStart, lineEnd);
        else {
            String line = source.subSequence(bodyStart, bodyEnd).toString();
            if (line.endsWith(":"))
                return parseProjectLine(lineStart, lineEnd, bodyStart, bodyEnd, source);
            else if (line.startsWith("- "))
                return parseTaskLine(lineStart, lineEnd, bodyStart, bodyEnd, source);
            else
                return parseRegularLine(lineStart, lineEnd, source);
        }
    }
    
    private ADocumentLevelNode parseRegularLine(int lineStart, int lineEnd, CharSequence source) {
        return ATextLine.extract(lineStart, lineEnd, source);
    }
    
    private AProjectLine parseProjectLine(int lineStart, int lineEnd, int bodyStart, int bodyEnd,
            CharSequence source) {
        return new AProjectLine(lineStart, lineEnd, AProjectName.extract(bodyStart, bodyEnd - 1, source));
    }
    
    private ATaskLine parseTaskLine(int lineStart, int lineEnd, int bodyStart, int bodyEnd, CharSequence source) {
        ATaskLine task = new ATaskLine(lineStart, lineEnd);
        task.addChild(new ATaskLeader(bodyStart, bodyStart + 1));
        
        int nameStart = bodyStart + 2;
        Matcher matcher = TAG.matcher(source);
        matcher.region(nameStart, bodyEnd);
        boolean found = matcher.find();
        int nameEnd = (found ? matcher.start() : bodyEnd);
        task.addChild(ATaskName.extract(nameStart, nameEnd, source));
        if (found)
            do {
                ATagName nameNode = ATagName.extract(matcher.start(1), matcher.end(1), source);
                String value = matcher.group(2);
                ARange valueRange = null;
                if (value != null)
                    valueRange = new ARange(matcher.start(2), matcher.end(2));
                else {
                    value = matcher.group(3);
                    if (value != null)
                        valueRange = new ARange(matcher.start(3), matcher.end(3));
                }
                ATagValue valueNode = (value == null ? null : ATagValue.extract(valueRange.start(), valueRange.end(), source));
                int tagStart = matcher.start() + 1 /* compensate for a space */; 
                task.addChild(new ATag(tagStart, matcher.end(), nameNode, valueNode));
            } while (matcher.find());
        return task;
    }
    
}
