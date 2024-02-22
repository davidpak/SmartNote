package com.smartnote.server.format;

import com.smartnote.server.format.nodes.BlockQuote;
import com.smartnote.server.format.nodes.BulletList;
import com.smartnote.server.format.nodes.Document;
import com.smartnote.server.format.nodes.FencedCodeBlock;
import com.smartnote.server.format.nodes.HardLineBreak;
import com.smartnote.server.format.nodes.Heading;
import com.smartnote.server.format.nodes.IndentedCodeBlock;
import com.smartnote.server.format.nodes.ListItem;
import com.smartnote.server.format.nodes.MarkdownNode;
import com.smartnote.server.format.nodes.OrderedList;
import com.smartnote.server.format.nodes.Paragraph;
import com.smartnote.server.format.nodes.SoftLineBreak;
import com.smartnote.server.format.nodes.Text;
import com.smartnote.server.format.nodes.ThematicBreak;

/**
 * <p>
 * Visits markdown nodes for processing.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see MarkdownNode
 */
public abstract class MarkdownVisitor {
    public void visit(BlockQuote md) {
        visitChildren(md);
    }

    public void visit(BulletList md) {
        visitChildren(md);
    }

    public void visit(Document md) {
        visitChildren(md);
    }

    public void visit(FencedCodeBlock md) {
        visitChildren(md);
    }

    public void visit(HardLineBreak md) {
        visitChildren(md);
    }

    public void visit(Heading md) {
        visitChildren(md);
    }

    public void visit(IndentedCodeBlock md) {
        visitChildren(md);
    }

    public void visit(ListItem md) {
        visitChildren(md);
    }

    public void visit(OrderedList md) {
        visitChildren(md);
    }

    public void visit(Paragraph md) {
        visitChildren(md);
    }

    public void visit(SoftLineBreak md) {
        visitChildren(md);
    }

    public void visit(Text md) {
        visitChildren(md);
    }

    public void visit(ThematicBreak md) {
        visitChildren(md);
    }

    /**
     * Accepts the children of the markdown node.
     * 
     * @param md The markdown node.
     */
    public void visitChildren(MarkdownNode md) {
        for (MarkdownNode child : md.getChildren())
            child.accept(this);
    }
}
