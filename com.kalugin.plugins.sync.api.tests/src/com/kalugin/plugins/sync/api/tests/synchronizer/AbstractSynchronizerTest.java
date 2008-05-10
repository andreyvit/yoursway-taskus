package com.kalugin.plugins.sync.api.tests.synchronizer;

import static com.google.common.collect.Maps.uniqueIndex;
import static com.kalugin.plugins.sync.api.synchronizer.SynchronizableTaskUtils.TASK_TO_ID;
import static com.kalugin.plugins.sync.api.tests.synchronizer.mocks.SynchronizableTaskImpl.createIdAssigner;
import static com.kalugin.plugins.sync.api.tests.utils.TestingUtils.optionalEntry;
import static com.kalugin.plugins.sync.api.tests.utils.TestingUtils.readTasks;
import static com.kalugin.plugins.sync.api.tests.utils.TestingUtils.requiredEntry;
import static com.kalugin.plugins.sync.api.tests.utils.TestingUtils.tasksToString;
import static com.kalugin.plugins.sync.api.tests.utils.TestingUtils.withDefault;
import static com.yoursway.utils.JavaStackFrameUtils.callerStackTraceElementOutside;
import static com.yoursway.utils.JavaStackFrameUtils.packageName;
import static com.yoursway.utils.JavaStackFrameUtils.removeBasePackageName;
import static com.yoursway.utils.YsFileUtils.joinPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTag;
import com.kalugin.plugins.sync.api.synchronizer.SynchronizableTask;
import com.kalugin.plugins.sync.api.synchronizer.SynchronizationResult;
import com.kalugin.plugins.sync.api.synchronizer.Synchronizer;
import com.kalugin.plugins.sync.api.synchronizer.TaskId;
import com.kalugin.plugins.sync.api.synchronizer.changes.Change;
import com.kalugin.plugins.sync.api.synchronizer.changes.ChangeVisitor;
import com.kalugin.plugins.sync.api.tests.AllTests;
import com.kalugin.plugins.sync.api.tests.synchronizer.mocks.SynchronizableTaskImpl;
import com.kalugin.plugins.sync.api.tests.utils.IdAssigner;
import com.yoursway.utils.YsFileUtils;

public class AbstractSynchronizerTest {
    
    protected void go() throws Exception {
        this.path = joinPath("tests/", calculatePath());
        run();
    }
    
    private void run() throws IOException {
        IdAssigner<String> idAssigner = createIdAssigner();
        
        URL oldLocalTasksEntry = requiredEntry(joinPath(path(), "0-local.txt"));
        URL oldRemoteTasksEntry = requiredEntry(joinPath(path(), "0-remote.txt"));
        URL localTasksEntry = optionalEntry(joinPath(path(), "1-local.txt"));
        URL remoteTasksEntry = optionalEntry(joinPath(path(), "1-remote.txt"));
        URL expectedLocalTasksEntry = optionalEntry(joinPath(path(), "R-local.txt"));
        URL expectedRemoteTasksEntry = optionalEntry(joinPath(path(), "R-remote.txt"));
        
        List<SynchronizableTask> oldLocalTasks = readTasks(oldLocalTasksEntry, idAssigner);
        List<SynchronizableTask> oldRemoteTasks = readTasks(oldRemoteTasksEntry, idAssigner);
        List<SynchronizableTask> localTasks = withDefault(readTasks(localTasksEntry, idAssigner),
                oldLocalTasks);
        List<SynchronizableTask> remoteTasks = withDefault(readTasks(remoteTasksEntry, idAssigner),
                oldRemoteTasks);
        
        Synchronizer synchronizer = new Synchronizer();
        synchronizer.setOldLocalTasks(oldLocalTasks);
        synchronizer.setOldRemoteTasks(oldRemoteTasks);
        synchronizer.setNewLocalTasks(localTasks);
        synchronizer.setNewRemoteTasks(remoteTasks);
        SynchronizationResult result = synchronizer.synchronize();
        
        // Expected string is always taken directly from one of the input files to prevent
        // false test passes caused by possible parsing/formatting errors
        
        if (expectedLocalTasksEntry != null || expectedRemoteTasksEntry != null) {
            Collection<SynchronizableTask> resultingLocalTasks = apply(localTasks, result
                    .getChangesToApplyLocally());
            Collection<SynchronizableTask> resultingRemoteTasks = apply(remoteTasks, result
                    .getChangesToApplyRemotely());
            
            String actual = format(resultingLocalTasks, resultingRemoteTasks);
            
            String expected = format(read(expectedLocalTasksEntry, localTasksEntry, oldLocalTasksEntry),
                    read(expectedRemoteTasksEntry, remoteTasksEntry, oldRemoteTasksEntry));
            assertEquals(expected, actual);
        } else {
            fail("No verification method used.");
        }
    }
    
    private String read(URL... entries) throws IOException {
        for (URL entry : entries)
            if (entry != null)
                return YsFileUtils.readAsStringAndClose(entry.openStream());
        return "";
    }
    
    private String format(Collection<SynchronizableTask> localTasks,
            Collection<SynchronizableTask> remoteTasks) {
        return format(tasksToString(localTasks), tasksToString(remoteTasks));
    }
    
    private String format(String local, String remote) {
        return "LOCAL:\n" + local.trim() + "\nREMOTE:\n" + remote.trim();
    }
    
    private String path;
    
    private Collection<SynchronizableTask> apply(List<SynchronizableTask> tasks, Collection<Change> changes) {
        final Map<TaskId, SynchronizableTask> idsToTasks = uniqueIndex(tasks, TASK_TO_ID);
        for (Change change : changes) {
            change.accept(new ChangeVisitor() {
                
                public void visitAddition(SynchronizableTask task) {
                    SynchronizableTask previousTask = idsToTasks.put(task.getId(), task);
                    if (previousTask != null)
                        throw new IllegalArgumentException("Task " + task + " already existed as "
                                + previousTask);
                }
                
                public void visitRemoval(SynchronizableTask task) {
                    SynchronizableTask oldTask = idsToTasks.remove(task.getId());
                    if (oldTask == null)
                        throw new IllegalArgumentException("Task " + task + " did not exist");
                }
                
                public void visitRename(SynchronizableTask newerTask) {
                    SynchronizableTask oldTask = idsToTasks.put(newerTask.getId(), newerTask);
                    if (oldTask == null)
                        throw new IllegalArgumentException("Task " + newerTask + " did not exist");
                }
                
                public void visitTagAddition(SynchronizableTask task, SynchronizableTag tag) {
                    getTask(task).addTag(tag);
                }
                
                public void visitTagRemoval(SynchronizableTask task, SynchronizableTag tag) {
                    getTask(task).removeTag(tag.getName());
                }
                
                public void visitTagValueChange(SynchronizableTask task, SynchronizableTag newerTag) {
                    getTask(task).removeTag(newerTag.getName()).addTag(newerTag);
                }
                
                private SynchronizableTaskImpl getTask(SynchronizableTask task) {
                    SynchronizableTaskImpl existingTask = (SynchronizableTaskImpl) idsToTasks.put(task
                            .getId(), task);
                    if (existingTask == null)
                        throw new IllegalArgumentException("Task " + task + " did not exist");
                    return existingTask;
                }
                
            });
        }
        return idsToTasks.values();
    }
    
    private String calculatePath() {
        StackTraceElement el = callerStackTraceElementOutside(AbstractSynchronizerTest.class);
        String methodName = el.getMethodName();
        String packageAndClassName = removeBasePackageName(el.getClassName(), packageName(AllTests.class));
        String path = packageAndClassName.replaceAll("\\.", "/") + "/" + methodName;
        return path;
    }

    private String path() {
        return path;
    }
    
}
