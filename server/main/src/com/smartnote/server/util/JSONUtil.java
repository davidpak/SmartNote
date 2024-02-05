package com.smartnote.server.util;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Stream;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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
        return toArray(Stream.of(array), JSONSerializable::writeJSON);
    }

    /**
     * <p>
     * Converts an array of objects to a JSON array.
     * </p>
     * 
     * @param <T> The type of the objects in the array.
     * @param array The array of objects.
     * @param mapper A function that maps the objects to JsonElements.
     * @return The JSON array.
     */
    public static <T> JsonArray toArray(T[] array, Function<T, JsonElement> mapper) {
        return toArray(Stream.of(array), mapper);
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
        return toArray(collection, JSONSerializable::writeJSON);
    }

    /**
     * <p>
     * Converts a collection of objects to a JSON array.
     * </p>
     * 
     * @param <T> The type of the objects in the collection.
     * @param collection The collection of objects.
     * @param mapper A function that maps the objects to JsonElements.
     * @return The JSON array.
     */
    public static <T> JsonArray toArray(Collection<T> collection, Function<T, JsonElement> mapper) {
        return toArray(collection.stream(), mapper);
    }

    /**
     * <p>
     * Reduces a stream of JSONSerializable objects to a JSON array.
     * </p>
     * 
     * @param <T> The type of objects in the stream.
     * @param stream The stream of objects.
     * @param mapper A function that maps the objects to JsonElements.
     * @return The JSON array.
     */
    public static <T> JsonArray toArray(Stream<T> stream, Function<T, JsonElement> mapper) {
        return stream.map(mapper)
                .collect(JsonArray::new, JsonArray::add,
                        JsonArray::addAll);
    }

    public static JsonObject toObject(Entry<String, ? extends JSONSerializable<?>>[] array) {
        return toObject(Stream.of(array));
    }

    public static <T> JsonObject toObject(T[] array, Function<T, Entry<String, ? extends JSONSerializable<?>>> mapper) {
        return toObject(Stream.of(array), mapper);
    }

    public static JsonObject toObject(Collection<Entry<String, ? extends JSONSerializable<?>>> collection) {
        return toObject(collection.stream());
    }

    public static <T> JsonObject toObject(Collection<T> collection, Function<T, Entry<String, ? extends JSONSerializable<?>>> mapper) {
        return toObject(collection.stream(), mapper);
    }

    public static <T> JsonObject toObject(Stream<T> stream, Function<T, Entry<String, ? extends JSONSerializable<?>>> mapper) {
        return toObject(stream.map(mapper));   
    }

    public static <T> JsonObject toObject(Stream<Entry<String, ? extends JSONSerializable<?>>> stream) {
        return stream.reduce(new JsonObject(), entryMapper(), objectMerger());
    }

    public static BiFunction<JsonObject, Entry<String, ? extends JSONSerializable<?>>, JsonObject> entryMapper() {
        return (o, e) -> {
            o.add(e.getKey(), e.getValue().writeJSON());
            return o;
        };
    }

    public static BinaryOperator<JsonObject> objectMerger() {
        return (o1, o2) -> {
            o2.entrySet().forEach(e -> o1.add(e.getKey(), e.getValue()));
            return o1;
        };
    }

    // Prevent instantiation
    private JSONUtil() {
    }
}
