package com.smartnote.server.cli;

public class NoSuchSwitchException extends RuntimeException {
    public NoSuchSwitchException(String message) {
        super(message);
    }

    public NoSuchSwitchException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchSwitchException(Throwable cause) {
        super(cause);
    }

    public NoSuchSwitchException() {
        super();
    }
}
