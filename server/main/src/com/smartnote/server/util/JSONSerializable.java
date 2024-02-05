package com.smartnote.server.util;

import com.google.gson.JsonElement;

/**
 * <p>
 * An object that can be serialized to and from JSON.
 * </p>
 * 
 * @author Ethan Vrhel
 */
public interface JSONSerializable<T extends JsonElement> {
    /**
     * Serializes the object to a JSON value.
     * 
     * @return The JSON value.
     */
    T writeJSON();

    /**
     * Deserializes the object from a JSON value.
     * 
     * @param json The JSON value.
     * 
     * @throws UnsupportedOperationException If the object does not support
     *                                       deserialization.
     */
    void loadJSON(T json) throws UnsupportedOperationException;
}
