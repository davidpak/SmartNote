package com.smartnote.server.format.rtf;

import org.commonmark.node.*;
import org.commonmark.renderer.Renderer;

/**
 * <p>Converts Markdown to RTF.</p>
 * 
 * @author Ethan Vrhel
 * @see org.commonmark.renderer.Renderer
 * @see RTFVisitor
 */
public class RTFRenderer implements Renderer {
    
    @Override
    public void render(Node node, Appendable output) {
        node.accept(new RTFVisitor(output));
    }

    @Override
    public String render(Node node) {
        StringBuilder builder = new StringBuilder();
        render(node, builder);
        return builder.toString();
    }
 }
