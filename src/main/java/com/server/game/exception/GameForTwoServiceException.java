package com.server.game.exception;

public class GameForTwoServiceException extends Exception {
    public GameForTwoServiceException() {
        super();
    }

    public GameForTwoServiceException(String message) {
        super(message);
    }

    public GameForTwoServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public GameForTwoServiceException(Throwable cause) {
        super(cause);
    }

    protected GameForTwoServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
