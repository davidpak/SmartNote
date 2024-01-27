package com.smartnote.testing;

/**
 * Used to override virtual machine exit calls.
 */
public class ExitException extends RuntimeException {
    private final int status;

    public ExitException() {
        this(0);
    }

    public ExitException(int status) {
        super();
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
