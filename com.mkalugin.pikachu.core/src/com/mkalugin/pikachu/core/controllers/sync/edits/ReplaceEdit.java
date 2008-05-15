package com.mkalugin.pikachu.core.controllers.sync.edits;

public class ReplaceEdit extends Edit {
    
    private int offset;
    private final int length;
    private final String text;
    
    public ReplaceEdit(int offset, int length, String text) {
        this.offset = offset;
        this.length = length;
        this.text = text;
    }
    
    @Override
    public int apply(StringBuilder builder) {
        builder.replace(offset, offset + length, text);
        return text.length() - length;
    }
    
    @Override
    int offset() {
        return offset;
    }
    
    @Override
    void fixup(int offset) {
        this.offset += offset;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + length;
        result = prime * result + offset;
        result = prime * result + ((text == null) ? 0 : text.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ReplaceEdit other = (ReplaceEdit) obj;
        if (length != other.length)
            return false;
        if (offset != other.offset)
            return false;
        if (text == null) {
            if (other.text != null)
                return false;
        } else if (!text.equals(other.text))
            return false;
        return true;
    }
    
}
