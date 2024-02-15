package com.smartnote.server.format;

import com.smartnote.server.format.notion.NotionBlock;

/**
 * <p>
 * Describes a rich text style.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see NotionBlock
 */
public record Style(boolean bold, boolean italic, boolean strikethrough, boolean underline, boolean code, String link) {
    /**
     * Create a default style.
     */
    public Style() {
        this(false, false, false, false, false, null);
    }

    /**
     * Return a new style with bold enabled.
     * 
     * @return The new style.
     */
    public Style withBold() {
        return new Style(true, italic, strikethrough, underline, code, link);
    }

    /**
     * Return a new style with italic enabled.
     * 
     * @return The new style.
     */
    public Style withItalic() {
        return new Style(bold, true, strikethrough, underline, code, link);
    }

    /**
     * Return a new style with strikethrough enabled.
     * 
     * @return The new style.
     */
    public Style withStrikethrough() {
        return new Style(bold, italic, true, underline, code, link);
    }

    /**
     * Return a new style with underline enabled.
     * 
     * @return The new style.
     */
    public Style withUnderline() {
        return new Style(bold, italic, strikethrough, true, code, link);
    }

    /**
     * Return a new style with code enabled.
     * 
     * @return The new style.
     */
    public Style withCode() {
        return new Style(bold, italic, strikethrough, underline, true, link);
    }

    /**
     * Return a new style with a link.
     * 
     * @param link The link.
     * @return The new style.
     */
    public Style withLink(String link) {
        return new Style(bold, italic, strikethrough, underline, code, link);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;

        if (o instanceof Style s) {
            boolean b = s.bold == bold && s.italic == italic && s.strikethrough == strikethrough
                    && s.underline == underline
                    && s.code == code;

            if (!b)
                return false;

            if (s.link == null)
                return link == null;

            return s.link.equals(link);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Boolean.hashCode(bold) + Boolean.hashCode(italic) + Boolean.hashCode(strikethrough)
                + Boolean.hashCode(underline) + Boolean.hashCode(code) + link.hashCode();
    }

    @Override
    public String toString() {
        return "Style [bold=" + bold + ", italic=" + italic + ", strikethrough=" + strikethrough + ", underline="
                + underline + ", code=" + code + ", link=" + link + "]";
    }
}