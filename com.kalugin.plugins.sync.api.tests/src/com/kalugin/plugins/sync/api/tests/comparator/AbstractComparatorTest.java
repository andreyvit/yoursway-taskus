package com.kalugin.plugins.sync.api.tests.comparator;

import static com.kalugin.plugins.sync.api.tests.synchronizer.mocks.SynchronizableTaskImpl.createIdAssigner;
import static com.kalugin.plugins.sync.api.tests.utils.TestingUtils.read;
import static com.kalugin.plugins.sync.api.tests.utils.TestingUtils.readTasks;
import static com.kalugin.plugins.sync.api.tests.utils.TestingUtils.requiredEntry;
import static com.yoursway.utils.JavaStackFrameUtils.callerStackTraceElementOutside;
import static com.yoursway.utils.JavaStackFrameUtils.packageName;
import static com.yoursway.utils.JavaStackFrameUtils.removeBasePackageName;
import static com.yoursway.utils.YsFileUtils.joinPath;
import static com.yoursway.utils.YsStrings.sortedToStringWithNewLines;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTask;
import com.kalugin.plugins.sync.api.synchronizer.changes.Change;
import com.kalugin.plugins.sync.api.synchronizer.changes.Changes;
import com.kalugin.plugins.sync.api.tests.AllTests;
import com.kalugin.plugins.sync.api.tests.utils.IdAssigner;

public class AbstractComparatorTest {

    private String path;
    
    protected void go() throws Exception {
        path = joinPath("tests/", calculatePath());
        run();
    }
    
    private void run() throws IOException {
        IdAssigner<String> idAssigner = createIdAssigner();
        
        URL oldEntry = requiredEntry(joinPath(path, "0.txt"));
        URL newEntry = requiredEntry(joinPath(path, "1.txt"));
        URL changesEntry = requiredEntry(joinPath(path, "R-changes.txt"));
        
        List<SynchronizableTask> oldTasks = readTasks(oldEntry, idAssigner);
        List<SynchronizableTask> newTasks = readTasks(newEntry, idAssigner);
        
        Collection<Change> changes = Changes.compare(oldTasks, newTasks);
        
        String actual = sortedToStringWithNewLines(changes).trim();
        String expected = read(changesEntry).trim();
        assertEquals(expected, actual);
    }
    
    private String calculatePath() {
        StackTraceElement el = callerStackTraceElementOutside(AbstractComparatorTest.class);
        String packageAndClassName = removeBasePackageName(el.getClassName(), packageName(AllTests.class));
        return packageAndClassName.replaceAll("\\.", "/") + "/" + el.getMethodName();
    }
    
}
