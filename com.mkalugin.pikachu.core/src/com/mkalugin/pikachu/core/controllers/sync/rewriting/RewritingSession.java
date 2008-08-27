package com.mkalugin.pikachu.core.controllers.sync.rewriting;

import static com.mkalugin.pikachu.core.workspace.ParsingUtils.adjustStartBySkippingWhitespaceBackward;

import com.mkalugin.pikachu.core.ast.ARange;
import com.mkalugin.pikachu.core.ast.ATaskLine;
import com.mkalugin.pikachu.core.ast.ATaskName;
import com.mkalugin.pikachu.core.controllers.sync.edits.CompoundEdit;
import com.mkalugin.pikachu.core.controllers.sync.edits.ReplaceEdit;
import com.mkalugin.pikachu.core.model.Document;
import com.mkalugin.pikachu.core.model.document.structure.MTag;
import com.mkalugin.pikachu.core.model.document.structure.MTask;
import com.mkalugin.pikachu.core.model.document.structure.MText;
import com.mkalugin.pikachu.core.workspace.ParsingUtils;

public class RewritingSession {
    
    private final CharSequence source;
    private CompoundEdit compound;
    
    public RewritingSession(CharSequence source) {
        this.source = source;
        compound = new CompoundEdit();
    }
    
    public void addTask(MTask task, MText after) {
        insertLine(after.range().end(), serializeTaskLine(task));
    }
    
    private void insertLine(int afterOffset, String line) {
        int sourceEnd = source.length();
        int pos;
        for (pos = afterOffset; pos < sourceEnd; pos++)
            if (isEndOfLine(charAt(pos)))
                break;
        while (pos < sourceEnd && isEndOfLine(charAt(pos)))
            pos++;
        if (pos == sourceEnd)
            line = "\n" + line;
        compound.add(new ReplaceEdit(pos, 0, line)); // + "\n"
    }
    
    private String serializeTaskLine(MTask task) {
        StringBuilder result = new StringBuilder();
        result.append("- ").append(task.getName());
        for (MTag tag : task.getTags()) {
            result.append(' ');
            serializeTag(tag, result);
        }
        return result.toString();
    }
    
    private String serializeTag(MTag tag) {
        StringBuilder result = new StringBuilder();
        serializeTag(tag, result);
        return result.toString();
    }
    
    private void serializeTag(MTag tag, StringBuilder result) {
        result.append('@').append(tag.getName());
        String value = tag.getValue();
        if (value != null) {
            if (value.contains("("))
                result.append(':').append(value);
            else
                result.append('(').append(value).append(')');
        }
    }

    public String commit() {
        StringBuilder builder = new StringBuilder(source);
        compound.apply(builder);
        return builder.toString();
    }
    
    char charAt(int offset) {
        return source.charAt(offset);
    }
    
    String substring(int start, int end) {
        return source.subSequence(start, end).toString();
    }
    
    boolean isEndOfLine(char ch) {
        return ch == '\n' || ch == '\r';
    }
    
    char matchingEoln(char ch) {
        if (ch == '\n')
            return '\r';
        if (ch == '\r')
            return '\n';
        throw new IllegalArgumentException("Not a eoln: " + ch);
    }

    public void changeTagValue(MTag tag, MTag newerTag) {
        ARange range = tag.getNode().range();
        compound.add(new ReplaceEdit(range.start(), range.length(), 
                serializeTag(newerTag)));
        
    }
    
    ARange adjustRangeStartBySkippingWhitespaceBackward(ARange range) {
        int start = adjustStartBySkippingWhitespaceBackward(0, range.start() - 1, source);
        return new ARange(start, range.end());
    }
    
    public void removeTag(MTag tag) {
        ARange range = adjustRangeStartBySkippingWhitespaceBackward(tag.getNode().range());
        compound.add(new ReplaceEdit(range.start(), range.length(), ""));
    }
    
    public void insertTag(MTask task, MTag tag) {
        ARange range = task.getNode().range();
        int end = range.end();
        String text = serializeTag(tag);
        text = " " + text;
//        if (end < source.length() && !Character.isWhitespace(charAt(end)))
        compound.add(new ReplaceEdit(end, 0, text));
    }
    
    private int includeEoln(int end) {
        int length = source.length();
        if (end >= length)
            return end;
        char ch = charAt(end);
        if (!isEndOfLine(ch))
            return end;
        ++end;
        if (end >= length)
            return end;
        char ch2 = matchingEoln(ch);
        if (charAt(end) == ch2)
            return end + 1;
        else
            return end;
    }
    
    private ARange includeEoln(ARange range) {
        return new ARange(range.start(), includeEoln(range.end()));
    }

    public void removeTask(ATaskLine taskLine) {
        ARange range = includeEoln(taskLine.range());
        compound.add(new ReplaceEdit(range.start(), range.length(), ""));
    }

    public void renameTask(ATaskLine node, MTask wrap) {
        ATaskName name = node.name();
        ARange range = name.range();
        compound.add(new ReplaceEdit(range.start(), range.length(), wrap.getName()));
    }
    
}
