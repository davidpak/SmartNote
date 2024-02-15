package com.smartnote.server.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

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
     * Retrieve a string from a JsonObject or <code>null</code> if the key
     * does not exist or the value is not a string.
     * 
     * @param json The JsonObject.
     * @param key The key.
     * @return The string or <code>null</code>.
     */
    public static String getStringOrNull(JsonObject json, String key) {
        JsonElement element = json.get(key);
        if (element == null)
            return null;
        if (!element.isJsonPrimitive())
            return null;

        JsonPrimitive primitive = element.getAsJsonPrimitive();
        if (!primitive.isString())
            return null;

        return primitive.getAsString();
    }

    /**
     * Retrieve a string from a JsonObject or throw an exception if the key
     * does not exist or the value is not a string.
     * 
     * @param json The JsonObject.
     * @param key The key.
     * @return The string.
     * @throws IllegalArgumentException If the key does not exist or the value
     *                                  is not a string.
     */
    public static String getStringOrException(JsonObject json, String key) throws IllegalArgumentException {
        String string = getStringOrNull(json, key);
        if (string == null)
            throw new IllegalArgumentException(key);
        return string;
    }

    /**
     * Retrieve a JsonObject from a JsonObject or <code>null</code> if the key
     * does not exist or the value is not a JsonObject.
     * 
     * @param json The JsonObject.
     * @param key The key.
     * @return The JsonObject or <code>null</code>.
     */
    public static JsonObject getObjectOrNull(JsonObject json, String key) {
        JsonElement element = json.get(key);
        if (element == null)
            return null;
        if (!element.isJsonObject())
            return null;
        return element.getAsJsonObject();
    }

    /**
     * Retrieve a JsonObject from a JsonObject or an empty JsonObject if the key
     * does not exist or the value is not a JsonObject.
     * 
     * @param json The JsonObject.
     * @param key The key.
     * @return The JsonObject or an empty JsonObject.
     */
    public static JsonObject getObjectOrEmpty(JsonObject json, String key) {
        JsonObject object = getObjectOrNull(json, key);
        if (object == null)
            return new JsonObject();
        return object;
    }

    /**
     * Retrieve a boolean primitive from a JsonObject or <code>false</code> if
     * the key does not exist or the value is not a boolean.
     * 
     * @param json The JsonObject.
     * @param key The key.
     * @return The boolean or <code>false</code>.
     */
    public static boolean getBooleanOrFalse(JsonObject json, String key) {
        JsonElement element = json.get(key);
        if (element == null)
            return false;
        if (!element.isJsonPrimitive())
            return false;
        JsonPrimitive primitive = element.getAsJsonPrimitive();
        if (!primitive.isBoolean())
            return false;
        return primitive.getAsBoolean();
    }

    // Prevent instantiation
    private JSONUtil() {
    }
}
