package com.smartnote.server.cli;

/**
 * <p>Thrown when a switch is not found.</p>
 * 
 * @author Ethan Vrhel
 * @see CommandLineParser
 */
public class NoSuchSwitchException extends ExitEarlyEarlyException {
    public NoSuchSwitchException(String message) {
        super(1, message);
    }

    public NoSuchSwitchException(String message, Throwable cause) {
        super(1, message, cause);
    }

    public NoSuchSwitchException(Throwable cause) {
        super(1, cause);
    }

    public NoSuchSwitchException() {
        super(1);
    }
}
