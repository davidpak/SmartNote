package com.smartnote.server.format;

@FunctionalInterface
public interface MarkdownConverter<T> {
    T convert(ParsedMarkdown markdown);
}
