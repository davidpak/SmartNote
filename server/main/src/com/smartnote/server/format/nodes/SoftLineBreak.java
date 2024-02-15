package com.smartnote.server.format.nodes;

import com.smartnote.server.format.MarkdownVisitor;

public class SoftLineBreak extends MarkdownNode {

    @Override
    public void accept(MarkdownVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String getType() {
        return "softLineBreak";
    }

}
