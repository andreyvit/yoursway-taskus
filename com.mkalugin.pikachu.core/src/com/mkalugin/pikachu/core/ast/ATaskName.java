package com.mkalugin.pikachu.core.ast;

public class ATaskName extends ATextFragment {

    public ATaskName(int start, int end, String name) {
        super(start, end, name);
    }

    public static ANode extract(int start, int end, CharSequence source) {
        return new ATaskName(start, end, source.subSequence(start, end).toString());
    }
    
}
