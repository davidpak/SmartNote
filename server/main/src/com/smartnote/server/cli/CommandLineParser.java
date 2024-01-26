package com.smartnote.server.cli;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * <p>Parses command line arguments.</p>
 * 
 * <p>Register handlers for command line arguments. The handlers will be called when the argument
 * is encountered. The handler will be passed the CommandLineParser object and the argument.</p>
 * 
 * <p>In the handler, call the appropriate methods to parse the argument. For example, if a
 * switch requires an integer argument, call <code>parser.nextInt()</code>.</p>
 * 
 * @author Ethan Vrhel
 */
public class CommandLineParser implements Iterator<String> {
    private String[] args; // arguments
    private int index; // current index

    private Map<String, BiConsumer<CommandLineParser, String>> handlers; // long name to handler
    private Map<String, String> shortToLong; // short name to long name

    /**
     * Creates a new CommandLineParser object.
     * 
     * @param args The command line arguments.
     */
    public CommandLineParser(String[] args) {
        Objects.requireNonNull(args);
        if (args.length == 0)
            throw new IllegalArgumentException("args must not be empty");            

        this.args = new String[args.length - 1];
        System.arraycopy(args, 1, this.args, 0, this.args.length);

        this.index = 0;
        this.handlers = new HashMap<>();
        this.shortToLong = new HashMap<>();
    }

    /**
     * <p>Adds a handler for a command line argument.</p>
     * 
     * <p>The handler will be called when the argument is encountered. The handler will be passed
     * the CommandLineParser object and the argument. Handlers should call the appropriate methods
     * to parse the argument. For example, if a switch requires an integer argument, call
     * <code>parser.nextInt()</code>.</p>
     * 
     * <p>The argument should throw an exception on invalid format, missing arguments, or the
     * program should exit early. See {@link #parse()} for more information.</p>
     * 
     * @param arg The argument's long name (e.g. help). Will be prefixed with "--".
     * @param handler The handler.
     * @param shortName The argument's short name (e.g. h). Will be prefixed with "-". If
     * <code>null</code>, the argument will not have a short name.
     */
    public void addHandler(String arg, BiConsumer<CommandLineParser, String> handler, String shortName) {
        this.handlers.put(arg, handler);
        if (shortName != null)
            this.shortToLong.put(shortName, arg);
    }

    /**
     * Adds a handler for a command line argument. The argument will not have a short name.
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
     * Parses the command line arguments. Uses the handlers to parse the arguments.
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

    public String peek() throws NoSuchElementException {
        if (!hasNext())
            throw new NoSuchElementException();
        return args[index];
    }

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

    public int nextInt() throws NoSuchElementException, NumberFormatException {
        return Integer.parseInt(next());
    }

    public boolean isNextSwitch() {
        if (!hasNext())
            return false;
        String arg = peek();
        return arg.startsWith("-") && !arg.startsWith("--");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CommandLineParser[");
        for (int i = 0; i < args.length; i++) {
            sb.append(args[i]);
            if (i < args.length - 1)
                sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}
