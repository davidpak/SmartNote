package com.smartnote.server.util;

import com.google.gson.JsonObject;

/**
 * <p>
 * Obejcts that can be serialized to and from JSON objects.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see JSONSerializable
 */
public interface JSONObjectSerializable extends JSONSerializable<JsonObject> {

    /**
     * Writes the object to an existing JSON object.
     * 
     * @param json The object to write to.
     * @return <code>json</code>
     */
    JsonObject writeJSON(JsonObject json);

    @Override
    default JsonObject writeJSON() {
        return writeJSON(new JsonObject());
    }
}
