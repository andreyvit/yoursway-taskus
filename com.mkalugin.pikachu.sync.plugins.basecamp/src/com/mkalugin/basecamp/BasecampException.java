package com.mkalugin.basecamp;

public class BasecampException extends Exception {

    private static final long serialVersionUID = 1L;

    public BasecampException() {
        super();
    }

    public BasecampException(String message, Throwable cause) {
        super(message, cause);
    }

    public BasecampException(String message) {
        super(message);
    }

    public BasecampException(Throwable cause) {
        super(cause);
    }
    
}
