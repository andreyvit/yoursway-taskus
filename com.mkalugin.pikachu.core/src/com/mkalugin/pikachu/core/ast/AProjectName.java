package com.mkalugin.pikachu.core.ast;

public class AProjectName extends ATextFragment {
    
    public static AProjectName extract(int start, int end, CharSequence source) {
        return new AProjectName(start, end, source.subSequence(start, end).toString());
    }

    public AProjectName(int start, int end, String name) {
        super(start, end, name);
    }

}
