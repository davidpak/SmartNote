package com.smartnote.server.cli;

/**
 * <p>Thrown when a switch is not found. The switch
 * may be retrived with <code>getMessage()</code> or
 * <code>getSwitch()</code></p>
 * 
 * <p>It is implied that when this exception is thrown,
 * the program should exit.</p>
 * 
 * @author Ethan Vrhel
 * @see CommandLineParser
 */
public class NoSuchSwitchException extends ExitEarlyEarlyException {

    /**
     * Creates a new NoSuchSwitchException.
     * 
     * @param switchName The switch name.
     */
    public NoSuchSwitchException(String switchName) {
        super(1, switchName);
    }

    /**
     * Gets the switch name.
     * 
     * @return The switch name.
     */
    public String getSwitch() {
        return getMessage();
    }
}
