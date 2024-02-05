package com.smartnote.server.util;

import com.google.gson.JsonObject;
import com.smartnote.server.cli.CommandLineHandler;
import com.smartnote.server.cli.CommandLineParser;

/**
 * <p>
 * Basis for configuration objects. Configuration objects are used to store
 * configuration information for the server. They can be set through the command
 * line or through a JSON object.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see com.smartnote.server.cli.CommandLineHandler
 * @see com.smartnote.server.util.Validator
 * @see com.smartnote.server.util.JSONSerializable
 */
public abstract class AbstractConfig implements CommandLineHandler, Validator, JSONObjectSerializable {
    @Override
    public void validate() throws IllegalStateException {
        // Default implementation
    }

    @Override
    public void addHandlers(CommandLineParser parser) {
        // Default implementation
    }

    @Override
    public JsonObject writeJSON(JsonObject json) {
        // Default implementation
        return json;
    }

    @Override
    public void loadJSON(JsonObject json) {
        // Default implementation
    }
}
