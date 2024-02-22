package com.smartnote.server.format;

/**
 * <p>
 * Base interface for converting markdown to a specific format.
 * </p>
 * 
 * @param <T> The type to convert to.
 * @see ParsedMarkdown
 */
@FunctionalInterface
public interface MarkdownConverter<T> {

    /**
     * Converts the parsed markdown to the specified format.
     * 
     * @param markdown The parsed markdown.
     * @return The converted markdown.
     */
    T convert(ParsedMarkdown markdown);
}
