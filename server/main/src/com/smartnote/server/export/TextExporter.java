package com.smartnote.server.export;

import java.security.Permission;

import com.smartnote.server.format.text.TextConverter;

/**
 * Exporter for text files.
 * 
 * @author Ethan Vrhel
 * @see ResourceExporter
 */
@ExporterInfo(name = "txt")
public class TextExporter implements ResourceExporter {

    @Override
    public TextConverter createConverter(ExportOptions options, Permission permission) throws IllegalArgumentException, SecurityException {
        return new TextConverter();
    }

    @Override
    public String getExtension() {
        return "txt";
    }
}
