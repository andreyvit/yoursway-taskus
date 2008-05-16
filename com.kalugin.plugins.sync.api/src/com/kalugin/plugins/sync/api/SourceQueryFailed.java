package com.kalugin.plugins.sync.api;

public class SourceQueryFailed extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SourceQueryFailed() {
        super();
    }

    public SourceQueryFailed(String message, Throwable cause) {
        super(message, cause);
    }

    public SourceQueryFailed(String message) {
        super(message);
    }

    public SourceQueryFailed(Throwable cause) {
        super(cause);
    }
    
}
