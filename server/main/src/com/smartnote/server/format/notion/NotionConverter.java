package com.smartnote.server.format.notion;

import com.smartnote.server.format.MarkdownConverter;
import com.smartnote.server.format.ParsedMarkdown;

public class NotionConverter implements MarkdownConverter<NotionBlock> {

    @Override
    public NotionBlock convert(ParsedMarkdown markdown) {
        NotionVisitor visitor = new NotionVisitor();
        markdown.accept(visitor);
        return visitor.getBlock();
    }
}
