package com.smartnote.server.export;

import java.security.Permission;

import com.smartnote.server.format.rtf.RTFConverter;

/**
 * <p>Exports to RTF.</p>
 * 
 * @author Ethan Vrhel
 * @see ResourceExporter
 */
@ExporterInfo(name = "rtf")
public class RTFExporter implements ResourceExporter {
    @Override
    public RTFConverter createConverter(ExportOptions options, Permission permission) {
        return new RTFConverter();
    }

    @Override
    public String getExtension() {
        return "rtf";
    }
}
