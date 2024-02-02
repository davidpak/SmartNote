package com.smartnote.server.util;

import com.google.gson.JsonObject;
import com.smartnote.server.cli.CommandLineHandler;
import com.smartnote.server.cli.CommandLineParser;

/**
 * <p>Basis for configuration objects. Configuration objects are used to store
 * configuration information for the server. They can be set through the command
 * line or through a JSON object.</p>
 * 
 * @author Ethan Vrhel
 * @see com.smartnote.server.cli.CommandLineHandler
 * @see com.smartnote.server.util.Validator
 * @see com.smartnote.server.util.JSONSerializable
 */
public abstract class AbstractConfig implements CommandLineHandler, Validator, JSONSerializable {
    @Override
    public void validate() throws IllegalStateException {
        // Default implementation
    }

    @Override
    public void addHandlers(CommandLineParser parser) {
        // Default implementation
    }

    @Override
    public void writeJSON(JsonObject object) {
        // Default implementation
    }

    @Override
    public void loadJSON(JsonObject object) {
        // Default implementation
    }

    /**
     * Equivalent to <code>JSONSerializable.writeToObject(this, object)</code>.
     * 
     * @param object The object to write to.
     * @see com.smartnote.server.util.JSONSerializable#writeToObject(JSONSerializable, JsonObject)
     */
    public void writeToObject(JsonObject object) {
        JSONSerializable.writeToObject(this, object);
    }

    /**
     * Equivalent to <code>JSONSerializable.loadFromObject(this, object)</code>.
     * 
     * @param object The object to load from.
     * @see com.smartnote.server.util.JSONSerializable#loadFromObject(JSONSerializable, JsonObject)
     */
    public void loadFromObject(JsonObject object) {
        JSONSerializable.loadFromObject(this, object);
    }
}
