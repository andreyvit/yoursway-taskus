package com.mkalugin.pikachu.core.ast;

public class ATagName extends ATextFragment {

    public ATagName(int start, int end, String name) {
        super(start, end, name);
    }

    public static ATagName extract(int start, int end, CharSequence source) {
        return new ATagName(start, end, source.subSequence(start, end).toString());
    }

}
