package com.smartnote.server.export;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.security.Permission;

import com.google.gson.JsonObject;

/**
 * <p>
 * Handles exporting summaries. Implementations should annotate the
 * class with {@link ExporterInfo} and register it with the
 * {@link Exporters} class.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see com.smartnote.server.export.Exporters
 */
@FunctionalInterface
public interface Exporter {

    /**
     * Export the summary.
     * 
     * @param data The data to export.
     * @param options The options for the export.
     * @param permission The permission of the user.
     * @return A JSON object containing the export information. This
     * object will be sent to the client. See the documentation for
     * the data that should be included in the JSON object.
     * @throws SecurityException If the user does not have permission
     *                           to write to the export location.
     * @throws InvalidPathException If the output path is invalid.
     * @throws IOException If an I/O error occurs.
     * @throws ExportException If an error occurs during export. This
     *                         is a general exception for export
     *                         errors and there may be more specific
     *                         exceptions thrown.
     * @throws MalformedExportOptionsException If the export options
     *                                         are malformed.
     */
    JsonObject export(String data, JsonObject options, Permission permission) throws SecurityException, InvalidPathException, IOException, ExportException, MalformedExportOptionsException;
}
