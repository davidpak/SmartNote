package com.smartnote.server.format.nodes;

import java.util.List;

import com.smartnote.server.format.MarkdownVisitor;

public class BlockQuote extends MarkdownNode {
    public BlockQuote(List<MarkdownNode> children) {
        super(children);
    }

    @Override
    public void accept(MarkdownVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String getType() {
        return "blockQuote";
    }
}
