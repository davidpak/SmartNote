package com.smartnote.server.util;

import java.util.Collection;

import com.google.gson.JsonArray;

/**
 * <p>
 * Contains utility methods for working with JSON.
 * </p>
 * 
 * @author Ethan Vrhel
 * @see JSONSerializable
 */
public class JSONUtil {

    /**
     * <p>
     * Converts an array of JSONSerializable objects to a JSON array.
     * </p>
     * 
     * @param array The array of JSONSerializable objects.
     * @return The JSON array.
     */
    public static JsonArray toArray(JSONSerializable<? extends JSONSerializable<?>>[] array) {
        JsonArray jsonArray = new JsonArray();
        for (var serializable : array)
            jsonArray.add(serializable.writeJSON());
        return jsonArray;
    }

    /**
     * <p>
     * Converts a collection of JSONSerializable objects to a JSON array.
     * </p>
     * 
     * @param collection The collection of JSONSerializable objects.
     * @return The JSON array.
     */
    public static JsonArray toArray(Collection<? extends JSONSerializable<?>> collection) {
        return collection.stream()
                .map(JSONSerializable::writeJSON)
                .collect(JsonArray::new, JsonArray::add,
                        JsonArray::addAll);
    }

    // Prevent instantiation
    private JSONUtil() {
    }
}
