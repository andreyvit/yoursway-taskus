package com.mkalugin.pikachu.core.controllers.sync.rewriting;

import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTag;
import com.mkalugin.pikachu.core.ast.ARange;
import com.mkalugin.pikachu.core.controllers.sync.edits.CompoundEdit;
import com.mkalugin.pikachu.core.controllers.sync.edits.ReplaceEdit;
import com.mkalugin.pikachu.core.model.document.structure.MTag;
import com.mkalugin.pikachu.core.model.document.structure.MTask;
import com.mkalugin.pikachu.core.model.document.structure.MText;

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
        compound.add(new ReplaceEdit(pos, 0, line + "\n"));
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

    public void changeTagValue(MTag tag, MTag newerTag) {
        ARange range = tag.getRange();
        compound.add(new ReplaceEdit(range.start(), range.length(), 
                serializeTag(newerTag)));
        
    }
    
}
