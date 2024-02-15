package com.smartnote.server.format;

public abstract class MarkdownVisitor {
    public void visitBlockQuote(ParsedMarkdown md) {
        visitChildren(md);
    }

    public void visitBulletList(ParsedMarkdown md) {
        visitChildren(md);
    }

    public void visitDocument(ParsedMarkdown md) {
        visitChildren(md);
    }

    public void visitFencedCodeBlock(ParsedMarkdown md) {
    }

    public void visitHardLineBreak(ParsedMarkdown md) {
        visitChildren(md);
    }

    public void visitHeading(ParsedMarkdown md) {
        visitChildren(md);
    }

    public void visitThematicBreak(ParsedMarkdown md) {
        visitChildren(md);
    }

    public void visitIndentedCodeBlock(ParsedMarkdown md) {
    }

    public void visitListItem(ParsedMarkdown md) {
        visitChildren(md);
    }

    public void visitOrderedList(ParsedMarkdown md) {
        visitChildren(md);
    }

    public void visitParagraph(ParsedMarkdown md) {
        visitChildren(md);
    }

    public void visitSoftLineBreak(ParsedMarkdown md) {
        visitChildren(md);
    }

    public void visitText(ParsedMarkdown md) {
    }

    public void visitChildren(ParsedMarkdown md) {
        for (ParsedMarkdown child : md.getChildren())
            child.accept(this);
    }
}
