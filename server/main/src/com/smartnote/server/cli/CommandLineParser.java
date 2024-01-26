package com.smartnote.server.cli;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * <p>Parses command line arguments. This class is iterable, and can be
 * used to iterate over the arguments.</p>
 *  
 * @author Ethan Vrhel
 * @see CommandLineHandler
 * @see ExitEarlyEarlyException
 * @see NoSuchSwitchException
 */
public class CommandLineParser implements Iterator<String> {
    private String[] args; // arguments
    private int index; // current index

    private Map<String, BiConsumer<CommandLineParser, String>> handlers; // long name to handler
    private Map<String, String> shortToLong; // short name to long name

    /**
     * Creates a new CommandLineParser object.
     * 
     * @param args The command line arguments. Will be copied.
     */
    public CommandLineParser(String[] args) {
        this.args = new String[Objects.requireNonNull(args).length];
        System.arraycopy(args, 0, this.args, 0, args.length);

        this.index = 0;
        this.handlers = new HashMap<>();
        this.shortToLong = new HashMap<>();
    }

    /**
     * <p>Adds a handler for a command line argument. Arguments are case-insensitive.</p>
     * 
     * <p>The argument should throw an exception on invalid format, missing arguments, or the
     * program should exit early. See {@link #parse()} for more information.</p>
     * 
     * @param arg The argument's long name (e.g. help). Will be prefixed with "--".
     * @param handler The handler.
     * @param shortName The argument's short name (e.g. h). Will be prefixed with "-". If
     * <code>null</code>, the first non-duplicate character of the long name will be used.
     * @throws IllegalArgumentException If the argument already exists or the short name already
     * exists.
     */
    public void addHandler(String arg, BiConsumer<CommandLineParser, String> handler, String shortName) 
        throws IllegalArgumentException {

        arg = arg.toLowerCase();

        if (handlers.containsKey(arg))
            throw new IllegalArgumentException("Argument already exists");

        this.handlers.put(arg, handler);
        if (shortName == null) {
            int i;
            for (i = 0; i < arg.length(); i++) {
                char c = arg.charAt(i);
                if (!shortToLong.containsKey(String.valueOf(c))) {
                    shortName = String.valueOf(c);
                    break;
                }
            }
            
            if (i == arg.length())
                throw new IllegalArgumentException("No short name available");
        }
    }

    /**
     * Adds a handler for a command line argument. Arguments are case-insensitive.
     * 
     * @param arg The argument's long name (e.g. help). Will be prefixed with "--".
     * @param handler The handler.
     * 
     * @see #addHandler(String, BiConsumer, String)
     */
    public void addHandler(String arg, BiConsumer<CommandLineParser, String> handler) {
        addHandler(arg, handler, null);
    }

    /**
     * Adds a handler for a command line argument. Arguments are case-insensitive.
     * 
     * @param handler The handler.
     * 
     * @see #addHandler(String, BiConsumer, String)
     */
    public void addHandler(CommandLineHandler handler) {
        handler.addHandlers(this);
    }

    /**
     * Parses the command line arguments. Uses the handlers to parse the arguments. All arguments
     * are converted to lowercase before being passed to the handlers.
     * 
     * @return The remaining arguments after all switches have been parsed.
     * @throws ExitEarlyEarlyException If the program should exit early. This is not an error. The
     * exception will contain the exit code.
     * @throws NoSuchSwitchException If a switch does not exist. The message will be the switch name.
     * @throws IllegalArgumentException If an argument is invalid.
     * @throws NoSuchElementException If an argument is missing.
     */
    public String[] parse() throws ExitEarlyEarlyException, NoSuchSwitchException, IllegalArgumentException, NoSuchElementException {
        int old = index; // save index

        index = 0; // always start at the beginning
        try {
            while (hasNext()) {
                String arg = next();
                BiConsumer<CommandLineParser, String> handler;

                if (arg.startsWith("--")) {
                    String longName = arg.substring(2);
                    if (handlers.containsKey(longName))
                        handler = handlers.get(longName);
                    else
                        throw new NoSuchSwitchException(arg);
                } else if (arg.startsWith("-")) {
                    String shortName = arg.substring(1);
                    if (shortToLong.containsKey(shortName))
                        handler = handlers.get(shortToLong.get(shortName));
                    else
                        throw new NoSuchSwitchException(shortName);
                } else
                    break;

                handler.accept(this, arg);
            }
        } catch (Exception e) {
            index = old; // restore index
            throw e;
        }

        String[] remaining = new String[args.length - index];
        System.arraycopy(args, index, remaining, 0, remaining.length);

        index = old;

        return remaining;
    }

    @Override
    public boolean hasNext() {
        return index < args.length;
    }

    @Override
    public String next() throws NoSuchElementException {
        if (!hasNext())
            throw new NoSuchElementException();
        return args[index++];
    }

    /**
     * Looks at the next argument without advancing the index.
     * 
     * @return The next argument.
     * @throws NoSuchElementException If there are no more arguments.
     */
    public String peek() throws NoSuchElementException {
        if (!hasNext())
            throw new NoSuchElementException();
        return args[index];
    }

    /**
     * Checks if the next argument is an integer.
     * 
     * @return Whether or not the next argument is an integer.
     */
    public boolean hasNextInt() {
        if (!hasNext())
            return false;
        try {
            Integer.parseInt(peek());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Gets the next argument as an integer.
     * 
     * @return The next argument as an integer.
     * @throws NoSuchElementException If there are no more arguments.
     * @throws NumberFormatException If the next argument is not an integer.
     */
    public int nextInt() throws NoSuchElementException, NumberFormatException {
        return Integer.parseInt(next());
    }
}
