package com.smartnote.server.cli;

/**
 * <p>Thrown when the program should exit early. Stores an exit code.</p>
 * 
 * @author Ethan Vrhel
 * @see CommandLineParser
 */
public class ExitEarlyEarlyException extends RuntimeException {
    private int code;

    public ExitEarlyEarlyException(int code) {
        super();
        this.code = code;    
    }

    public ExitEarlyEarlyException(int code, String message) {
        super(message);
        this.code = code;
    }

    public ExitEarlyEarlyException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public ExitEarlyEarlyException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    /**
     * Get the code the program should exit with.
     * 
     * @return The code.
     */
    public int getCode() {
        return code;
    }
}
