package com.mkalugin.pikachu.core.tests;

import static com.mkalugin.pikachu.core.tests.TestingUtils.read;
import static com.mkalugin.pikachu.core.tests.TestingUtils.requiredEntry;
import static com.yoursway.utils.JavaStackFrameUtils.callerStackTraceElementOutside;
import static com.yoursway.utils.JavaStackFrameUtils.packageName;
import static com.yoursway.utils.JavaStackFrameUtils.removeBasePackageName;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URL;

import com.mkalugin.pikachu.core.ast.ADocument;
import com.mkalugin.pikachu.core.workspace.DocumentParser;
import com.yoursway.utils.YsFileUtils;

public class AbstractParserTests {
    
    private String path;

    protected void go() throws IOException {
        path = "tests/" + calculatePath();
        run();
    }
    
    private void run() throws IOException {
        URL dataEntry = requiredEntry(YsFileUtils.joinPath(path, "data.txt"));
        URL resultEntry = requiredEntry(YsFileUtils.joinPath(path, "result.txt"));
        String data = read(dataEntry);
        DocumentParser parser = new DocumentParser();
        ADocument document = parser.parse_old(data);
        
        String expected = read(resultEntry);
        String actual = document.toString();
        assertEquals(expected, actual);
    }
    
    private String calculatePath() {
        StackTraceElement el = callerStackTraceElementOutside(AbstractParserTests.class);
        String methodName = el.getMethodName();
        String packageAndClassName = removeBasePackageName(el.getClassName(), packageName(AllTests.class));
        return packageAndClassName.replaceAll("\\.", "/") + "/" + methodName;
    }

}
