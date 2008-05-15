package com.mkalugin.pikachu.core.controllers.sync;

import static com.google.common.base.Join.join;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static com.yoursway.utils.StringExtractor.WHITESPACE;
import static com.yoursway.utils.YsIterables.sort;
import static com.yoursway.utils.YsStrings.sortedToStringUsing;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Function;
import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTag;
import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTask;
import com.mkalugin.pikachu.core.model.document.structure.MTag;
import com.mkalugin.pikachu.core.model.document.structure.MTask;
import com.yoursway.utils.StringExtractor;

public class TaskPersistance {
    
    public static List<SynchronizableTask> parseTasks(String data, String idTagName) throws IOException {
        return parseTasks(data.trim().split("\n"), idTagName);
    }

    private static void parseTask(List<SynchronizableTask> result, String line, String idTagName) {
        StringExtractor extractor = new StringExtractor(line, WHITESPACE);
        String caption = extractor.requireAsString(Pattern.compile("[^@]*"));
        MTask ttt = new MTask();
        ttt.setName(caption.trim());
        
        Matcher tagMatch;
        while ((tagMatch = extractor.extract(Pattern.compile("^@(\\w+)(?::([^\\s]+)|\\(([^)]*)\\))?"))) != null) {
            String name = tagMatch.group(1);
            String value = tagMatch.group(2);
            if (value == null)
                value = tagMatch.group(3);
            MTag tag = new MTag();
            tag.setName(name);
            tag.setValue(value);
            ttt.addTag(tag);
        }
        
        extractor.mustBeEnd();
        
        LocalTask task = new LocalTask(ttt, idTagName);
        result.add(task);
    }

    private static List<SynchronizableTask> parseTasks(String[] lines, String idTagName) {
        List<SynchronizableTask> result = newArrayListWithCapacity(lines.length);
        for (String line : lines)
            if ((line = line.trim()).length() > 0)
                parseTask(result, line, idTagName);
        return result;
    }

    public static String tasksToString(Collection<? extends SynchronizableTask> tasks) {
        return join("\n", sort(transform(tasks, new Function<SynchronizableTask, String>() {

            public String apply(SynchronizableTask task) {
                StringBuilder result = new StringBuilder();
                result.append(task.getName());
                Collection<SynchronizableTag> tags = task.tags();
                if (!tags.isEmpty())
                    result.append(' ').append(join(" ", sort(transform(tags, new Function<SynchronizableTag, String>() {

                        public String apply(SynchronizableTag tag) {
                            StringBuilder result = new StringBuilder();
                            result.append('@').append(tag.getName());
                            String value = tag.getValue();
                            if (value != null)
                                result.append('(').append(value).append(')');
                            return result.toString();
                        }
                        
                    }))));
                return result.toString();
            }
            
        })));
    }

}
