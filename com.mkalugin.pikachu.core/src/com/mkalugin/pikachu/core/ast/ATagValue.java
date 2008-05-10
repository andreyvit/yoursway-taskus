package com.mkalugin.pikachu.core.ast;

public class ATagValue extends ATextFragment {

    public ATagValue(int start, int end, String value) {
        super(start, end, value);
    }

    public static ATagValue extract(int start, int end, CharSequence source) {
        return new ATagValue(start, end, source.subSequence(start, end).toString());
    }

}
