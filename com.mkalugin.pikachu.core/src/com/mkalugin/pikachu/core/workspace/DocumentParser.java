package com.mkalugin.pikachu.core.workspace;

import static com.yoursway.utils.assertions.Assert.assertion;
import static java.util.regex.Pattern.compile;

import java.util.Collection;
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
import com.mkalugin.pikachu.core.model.document.DocumentContent;
import com.mkalugin.pikachu.core.model.document.Tag;
import com.mkalugin.pikachu.core.model.document.Token;

public class DocumentParser {
    // Lexer
    
    private static final Pattern TAG = compile("@([^:(\\s@]*)(?::(\\S+)|\\(([^\\)]*)\\))?");
    
    private static final String TAGS = "(\\s+(@.+))?$";
    
    static final Pattern TAGLINE = compile("^@.+$");
    static final Pattern DIRECTIVE = compile("^Sync(hronize)? with (.+)$");
    
    static final Pattern TASK = compile("^-\\s+([^@]*)" + TAGS);
    static final Pattern GROUP = compile("^([^@]+):" + TAGS);
    static final Pattern SECTION = compile("^((\\w\\.)*\\w\\.?)\\s+([^@]+)" + TAGS);
    static final Pattern CHAPTER = compile("^(([A-Z0-9] )*[A-Z0-9])" + TAGS);
    
    private final String source;
    private final DocumentContentModelBuilder builder;
    
    private boolean started = false;
    
    private DocumentParser(String source) {
        if (source == null)
            throw new NullPointerException("source is null");
        
        this.source = source;
        builder = new DocumentContentModelBuilder(source.length());
    }
    
    @Deprecated
    public DocumentParser() {
        source = null;
        builder = null;
    }
    
    public static DocumentContent parse(String source) {
        return new DocumentParser(source).parseDocument();
    }
    
    private DocumentContent parseDocument() {
        if (started)
            throw new IllegalStateException("parsing of the document has been started already");
        started = true;
        
        int contentEnd = source.length();
        
        int lineStart = 0;
        while (lineStart < contentEnd) {
            int lineEnd = source.indexOf('\n', lineStart);
            if (lineEnd < 0)
                lineEnd = contentEnd;
            
            int bodyEnd = ParsingUtils.adjustEndBySkippingWhitespaceBackward(lineStart, lineEnd, source);
            int bodyStart = ParsingUtils.adjustStartBySkippingWhitespaceForward(lineStart, bodyEnd, source);
            assertion(bodyStart <= bodyEnd, "the start must be before the end");
            
            parseLine(lineStart, lineEnd, bodyStart, bodyEnd);
            
            // skip newline
            lineStart = lineEnd + 1;
        }
        
        builder.build();
        return builder.contentModel();
    }
    
    private void parseLine(int lineStart, int lineEnd, int bodyStart, int bodyEnd) {
        if (parseSeparator(bodyStart, bodyEnd))
            return;
        
        String line = source(bodyStart, bodyEnd);
        
        String indentString = source(lineStart, bodyStart);
        indentString = indentString.replaceAll("\t", "    ");
        int indent = (indentString.length() + 1) / 4; // 3 spaces rounds up to 4
        
        boolean a = parseChapter(bodyStart, bodyEnd, line) || parseTask(bodyStart, bodyEnd, line, indent)
                || parseGroup(bodyStart, bodyEnd, line) || parseSection(bodyStart, bodyEnd, line)
                || parseTagLine(bodyStart, line) || parseDirective(bodyStart, bodyEnd, line)
                || parseTextLine(line, indent, bodyStart, bodyEnd);
        
        assertion(a, "unknown lexeme");
    }
    
    private boolean parseSeparator(int bodyStart, int bodyEnd) {
        if (bodyStart == bodyEnd) {
            builder.addSeparator();
            return true;
        }
        return false;
    }
    
    private boolean parseTask(int bodyStart, int bodyEnd, String line, int indent) {
        Matcher matcher = TASK.matcher(line);
        if (matcher.matches()) {
            
            Token name = fetchToken(matcher, 1, bodyStart);
            builder.addTask(bodyStart, bodyEnd, name, indent);
            
            parseEndLineTags(matcher, 3, bodyStart);
            
            return true;
        }
        return false;
    }
    
    private boolean parseGroup(int bodyStart, int bodyEnd, String line) {
        Matcher matcher;
        matcher = GROUP.matcher(line);
        if (matcher.matches()) {
            
            Token name = fetchToken(matcher, 1, bodyStart);
            builder.addGroup(bodyStart, bodyEnd, name);
            
            parseEndLineTags(matcher, 3, bodyStart);
            
            return true;
        }
        return false;
    }
    
    private boolean parseSection(int bodyStart, int bodyEnd, String line) {
        Matcher matcher;
        matcher = SECTION.matcher(line);
        if (matcher.matches()) {
            
            Token index = fetchToken(matcher, 1, bodyStart);
            Token name = fetchToken(matcher, 3, bodyStart);
            builder.addSection(bodyStart, bodyEnd, name, index);
            
            parseEndLineTags(matcher, 5, bodyStart);
            
            return true;
        }
        return false;
    }
    
    private boolean parseChapter(int bodyStart, int bodyEnd, String line) {
        Matcher matcher;
        matcher = CHAPTER.matcher(line);
        if (matcher.matches()) {
            
            Token name = fetchToken(matcher, 1, bodyStart);
            builder.addChapter(bodyStart, bodyEnd, name);
            
            parseEndLineTags(matcher, 4, bodyStart);
            
            return true;
        }
        return false;
    }
    
    private boolean parseTagLine(int bodyStart, String line) {
        Matcher matcher = TAGLINE.matcher(line);
        if (matcher.matches()) {
            parseTags(line, bodyStart);
            
            return true;
        }
        return false;
    }
    
    private boolean parseDirective(int bodyStart, int bodyEnd, String line) {
        Matcher matcher = DIRECTIVE.matcher(line);
        if (matcher.matches()) {
            builder.addDirective(bodyStart, bodyEnd, line);
            
            return true;
        }
        return false;
    }
    
    private boolean parseTextLine(String line, int indent, int bodyStart, int bodyEnd) {
        builder.addTextLine(line, indent, bodyStart, bodyEnd);
        return true;
    }
    
    private Token fetchToken(Matcher matcher, int group, int bodyStart) {
        return new Token(matcher.group(group), matcher.start(group) + bodyStart, matcher.end(group)
                + bodyStart);
    }
    
    private void parseEndLineTags(Matcher matcher, int group, int bodyStart) {
        String tags = matcher.group(group);
        if (tags != null)
            parseTags(tags, bodyStart);
    }
    
    private Collection<Tag> parseTags(String tags, int bodyStart) {
        Matcher matcher = TAG.matcher(tags);
        boolean found = matcher.find();
        if (found)
            do {
                Token name = fetchToken(matcher, 1, bodyStart);
                Token value = null;
                if (matcher.group(2) != null)
                    value = fetchToken(matcher, 2, bodyStart);
                else if (matcher.group(3) != null)
                    value = fetchToken(matcher, 3, bodyStart);
                
                builder
                        .addTag(new Tag(name, value, bodyStart + matcher.start(0), bodyStart + matcher.end(0)));
            } while (matcher.find());
        return null;
    }
    
    private String source(int start, int end) {
        return source.subSequence(start, end).toString();
    }
    
    @Deprecated
    public ADocument parse_old(String source) {
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
            document.addChild(parseLine_old(lineStart, lineEnd, bodyStart, bodyEnd, source));
            lineStart = lineEnd + 1; // skip newline
        }
        return document;
    }
    
    @Deprecated
    private ADocumentLevelNode parseLine_old(int lineStart, int lineEnd, int bodyStart, int bodyEnd,
            CharSequence source) {
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
    
    @Deprecated
    private ADocumentLevelNode parseRegularLine(int lineStart, int lineEnd, CharSequence source) {
        return ATextLine.extract(lineStart, lineEnd, source);
    }
    
    @Deprecated
    private AProjectLine parseProjectLine(int lineStart, int lineEnd, int bodyStart, int bodyEnd,
            CharSequence source) {
        return new AProjectLine(lineStart, lineEnd, AProjectName.extract(bodyStart, bodyEnd - 1, source));
    }
    
    @Deprecated
    private ATaskLine parseTaskLine(int lineStart, int lineEnd, int bodyStart, int bodyEnd,
            CharSequence source) {
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
                ATagValue valueNode = (value == null ? null : ATagValue.extract(valueRange.start(),
                        valueRange.end(), source));
                int tagStart = matcher.start() + 1 /* compensate for a space */;
                task.addChild(new ATag(tagStart, matcher.end(), nameNode, valueNode));
            } while (matcher.find());
        return task;
    }
    
}
