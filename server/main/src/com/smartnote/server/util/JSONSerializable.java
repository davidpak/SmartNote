package com.smartnote.server.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * <p>Represents an object that can be serialized to JSON.</p>
 * 
 * @author Ethan Vrhel
 */
public interface JSONSerializable {

    /**
     * Writes the object to JSON.
     * 
     * @param object The object to write to.
     */
    void writeJSON(JsonObject object);

    /**
     * Writes the object to JSON.
     * 
     * @return The JSON object.
     */
    default JsonObject writeJSON() {
        JsonObject object = new JsonObject();
        writeJSON(object);
        return object;
    }

    /**
     * Loads the object from JSON. The implementation should not throw an exception
     * if the object is invalid, a member is missing, or a member of the wrong type.
     * Rather, it should do nothing and use the default value for the respective
     * member.
     * 
     * @param object The object to load from.
     */
    void loadJSON(JsonObject object);

    /**
     * Returns the name of the object.
     * 
     * @return The name.
     */
    default String getObjectName() {
        String name = getClass().getSimpleName();
        if (name.endsWith("Config"))
            name = name.substring(0, name.length() - 6);
        name = name.substring(0, 1).toLowerCase() + name.substring(1);
        return name;
    }

    /**
     * Serialize an object into the given object with the name
     * returned by <code>getObjectName()</code>.
     * 
     * @param sourceObject The object to serialize. Cannot be
     * <code>null</code>.
     * @param object The object to add to. Cannot be
     * <code>null</code>.
     */
    public static void writeToObject(JSONSerializable sourceObject, JsonObject destObject) {
        destObject.add(sourceObject.getObjectName(), sourceObject.writeJSON());
    }

    /**
     * Extract a member from the given object with the name returned
     * by <code>getObjectName()</code> and deserialize it into
     * <code>dest</code>. If no such member exists or the member is
     * not an object, <code>dest</code> remains unchanged.
     * 
     * @param dest The object to deserialize into. Cannot be
     * <code>null</code>.
     * @param sourceObject The object to load from. Cannot be
     * <code>null</code>.
     */
    public static void loadFromObject(JSONSerializable destObject, JsonObject sourceObject) {
        JsonElement element = sourceObject.get(destObject.getObjectName());
        if (element == null || !element.isJsonObject())
            return;
        destObject.loadJSON(element.getAsJsonObject());
    }
}
