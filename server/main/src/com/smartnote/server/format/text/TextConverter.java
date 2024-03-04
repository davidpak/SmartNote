package com.smartnote.server.format.text;

import com.smartnote.server.format.MarkdownConverter;
import com.smartnote.server.format.ParsedMarkdown;

public class TextConverter implements MarkdownConverter<String> {

    @Override
    public String convert(ParsedMarkdown markdown) {
        StringBuilder builder = new StringBuilder();
        TextVisitor visitor = new TextVisitor(builder);
        markdown.getDocument().accept(visitor);
        return builder.toString();
    }
    
}
