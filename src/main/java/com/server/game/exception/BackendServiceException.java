package com.server.game.exception;

public class BackendServiceException extends Exception{

    public BackendServiceException() {
        super();
    }

    public BackendServiceException(String message) {
        super(message);
    }

    public BackendServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public BackendServiceException(Throwable cause) {
        super(cause);
    }

    protected BackendServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
