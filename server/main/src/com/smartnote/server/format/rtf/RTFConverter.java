package com.smartnote.server.format.rtf;

import com.smartnote.server.format.MarkdownConverter;
import com.smartnote.server.format.ParsedMarkdown;

/**
 * <p>Converts Markdown to RTF.</p>
 * 
 * @author Ethan Vrhel
 * @see org.commonmark.renderer.Renderer
 * @see RTFVisitor
 */
public class RTFConverter implements MarkdownConverter<String> {

    @Override
    public String convert(ParsedMarkdown markdown) {
        StringBuilder builder = new StringBuilder();
        RTFVisitor visitor = new RTFVisitor(builder);
        markdown.getDocument().accept(visitor);
        return builder.toString();
    }
 }
