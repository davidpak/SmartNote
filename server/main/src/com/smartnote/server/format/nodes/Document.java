package com.smartnote.server.format.nodes;

import java.util.List;

import com.smartnote.server.format.MarkdownVisitor;

public class Document extends MarkdownNode {

    public Document(List<MarkdownNode> children) {
        super(children);
    }

    @Override
    public String getType() {
        return "document";
    }

    @Override
    public void accept(MarkdownVisitor visitor) {
        visitor.visit(this);
    }
}
