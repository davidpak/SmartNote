package com.smartnote.server.format;

import com.google.gson.JsonObject;

@FunctionalInterface
public interface MarkdownConverter<T> {
    T convert(ParsedMarkdown markdown);
}
