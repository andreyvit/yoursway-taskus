package com.kalugin.plugins.sync.api.tests.utils;

import static com.google.common.base.Join.join;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static com.yoursway.utils.YsIterables.sort;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import com.google.common.base.Functions;
import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTask;
import com.kalugin.plugins.sync.api.tests.internal.Activator;
import com.kalugin.plugins.sync.api.tests.synchronizer.mocks.IdAssigner;
import com.kalugin.plugins.sync.api.tests.synchronizer.mocks.TaskIdImpl;
import com.yoursway.utils.StringExtractor;
import com.yoursway.utils.YsFileUtils;

public class TestingUtils {
    
    public static URL optionalEntry(String path) {
        return Activator.getDefault().getBundle().getEntry(path);
    }
    
    public static URL requiredEntry(String path) {
        URL result = optionalEntry(path);
        if (result == null)
            throw new IllegalArgumentException("Entry " + path + " not found.");
        return result;
    }

    public static List<SynchronizableTask> readTasks(URL entry, IdAssigner<String> idAssigner) throws IOException {
        if (entry == null)
            return null;
        String data = YsFileUtils.readAsStringAndClose(entry.openStream());
        return parseTasks(data.trim().split("\n"), idAssigner);
    }

    public static List<SynchronizableTask> withDefault(List<SynchronizableTask> tasks,
            List<SynchronizableTask> defaultTasks) {
        return tasks == null ? newArrayList(defaultTasks) : tasks;
    }

    private static void parseTask(IdAssigner<String> idAssigner, List<SynchronizableTask> result, String line) {
        StringExtractor extractor = new StringExtractor(line, StringExtractor.WHITESPACE);
        String caption = extractor.requireWord();
        String idString = extractor.extract(Pattern.compile("^#\\d+"));
        int id = (idString == null ? idAssigner.idOf(caption) : Integer.valueOf(idString.substring(1)));
        result.add(new SynchronizableTaskImpl(caption, new TaskIdImpl(id)));
        extractor.mustBeEnd();
    }

    private static List<SynchronizableTask> parseTasks(String[] lines, IdAssigner<String> idAssigner) {
        List<SynchronizableTask> result = newArrayListWithCapacity(lines.length);
        for (String line : lines)
            if ((line = line.trim()).length() > 0)
                parseTask(idAssigner, result, line);
        return result;
    }

    public static String tasksToString(Collection<SynchronizableTask> resultingLocalTasks) {
        return join("\n", sort(transform(resultingLocalTasks, Functions.TO_STRING)));
    }

    public static String read(URL... entries) throws IOException {
        for (URL entry : entries)
            if (entry != null)
                return YsFileUtils.readAsStringAndClose(entry.openStream());
        return "";
    }
    
}
