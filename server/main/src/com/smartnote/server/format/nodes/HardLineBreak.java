package com.smartnote.server.format.nodes;

import com.smartnote.server.format.MarkdownVisitor;

public class HardLineBreak extends MarkdownNode {

    @Override
    public void accept(MarkdownVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String getType() {
        return "hardLineBreak";
    }

}
