package com.mkalugin.pikachu.core.workspace;

public class ParsingUtils {
    
    public static int adjustEndBySkippingWhitespaceBackward(int startLimit, int end, CharSequence source) {
        while (end > startLimit && Character.isWhitespace(source.charAt(end - 1)))
            --end;
        return end;
    }
    
    public static int adjustStartBySkippingWhitespaceForward(int start, int endLimit, CharSequence source) {
        while (start < endLimit && Character.isWhitespace(source.charAt(start)))
            ++start;
        return start;
    }
    
}
