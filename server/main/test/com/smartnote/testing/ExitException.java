package com.smartnote.testing;

/**
 * <p>Used to override virtual machine exit calls. Thrown
 * by the <code>SecurityManager</code> when <code>System.exit()</code>
 * is called.</p>
 * 
 * @author Ethan Vrhel
 * @see BaseTest
 */
public class ExitException extends RuntimeException {
    private final int status;

    public ExitException() {
        this(0);
    }

    public ExitException(int status) {
        super("System.exit() was called with code " + status);
        this.status = status;
    }

    /**
     * Gets the status code passed to System.exit().
     * 
     * @return the status code.
     */
    public int getStatus() {
        return status;
    }
}
