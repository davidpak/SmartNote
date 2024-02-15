package com.smartnote.server.format.notion;

import com.smartnote.server.format.MarkdownConverter;
import com.smartnote.server.format.ParsedMarkdown;

/**
 * <p>
 * Handles conversion of markdown to Notion's representation.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see NotionBlock
 */
public class NotionConverter implements MarkdownConverter<NotionBlock> {

    @Override
    public NotionBlock convert(ParsedMarkdown markdown) {
        NotionVisitor visitor = new NotionVisitor();
        markdown.getDocument().accept(visitor);
        return visitor.getBlock();
    }
}
