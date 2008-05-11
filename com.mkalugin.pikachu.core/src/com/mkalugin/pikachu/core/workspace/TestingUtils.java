package com.mkalugin.pikachu.core.workspace;

import static com.google.common.base.Functions.TO_STRING;
import static com.google.common.base.Join.join;
import static com.google.common.collect.Iterables.transform;

import java.util.List;

import com.google.common.base.Function;

public class TestingUtils {

    public static String containerToString(String container, List<?> children) {
        if (children.isEmpty())
            return container + " (empty)\n";
        else
            return container + ":\n-"
                    + join("\n-", transform(transform(children, TO_STRING), TestingUtils.FIXUP_NEWLINES)) + "\n";
    }

    public final static Function<String, String> FIXUP_NEWLINES = new Function<String, String>() {
        
        public String apply(String s) {
            return s.trim().replaceAll("\n", "\n-");
        }
        
    };
    
}
