package com.smartnote.server.format;

public abstract class MarkdownVisitor {
    public void visitBlockQuote(ParsedMarkdown blockQuote) {
        visitChildren(blockQuote);
    }

    public void visitBulletList(ParsedMarkdown bulletList) {
        visitChildren(bulletList);
    }

    public void visitCode(ParsedMarkdown code, Style style) {
    }

    public void visitDocument(ParsedMarkdown document) {
        visitChildren(document);
    }

    public void visitFencedCodeBlock(ParsedMarkdown fencedCodeBlock, Style style, String language) {
    }

    public void visitHardLineBreak(ParsedMarkdown hardLineBreak) {
        visitChildren(hardLineBreak);
    }

    public void visitHeading(ParsedMarkdown heading, int level) {
        visitChildren(heading);
    }

    public void visitThematicBreak(ParsedMarkdown thematicBreak) {
        visitChildren(thematicBreak);
    }

    public void visitIndentedCodeBlock(ParsedMarkdown indentedCodeBlock, Style style) {
    }

    public void visitListItem(ParsedMarkdown listItem) {
        visitChildren(listItem);
    }

    public void visitOrderedList(ParsedMarkdown orderedList) {
        visitChildren(orderedList);
    }

    public void visitParagraph(ParsedMarkdown paragraph) {
        visitChildren(paragraph);
    }

    public void visitSoftLineBreak(ParsedMarkdown softLineBreak) {
        visitChildren(softLineBreak);
    }

    public void visitChildren(ParsedMarkdown markdown) {
        for (ParsedMarkdown child : markdown.getChildren())
            child.accept(this);
    }
}
