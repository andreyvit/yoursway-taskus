package com.mkalugin.lighthouse;

public class LighthouseException extends Exception {

    private static final long serialVersionUID = 1L;

    public LighthouseException() {
        super();
    }

    public LighthouseException(String message, Throwable cause) {
        super(message, cause);
    }

    public LighthouseException(String message) {
        super(message);
    }

    public LighthouseException(Throwable cause) {
        super(cause);
    }
    
}
