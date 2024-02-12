package com.smartnote.server.util;

import java.util.AbstractMap;
import java.util.function.Function;

/**
 * <p>
 * Wrapper class around <code>AbstractMap.SimpleEntry</code>. This
 * is used for conciseness in the code.
 * </p>
 * 
 * @author Ethan Vrhel
 * @param <T> The key type
 * @param <U> The value type
 * 
 * @see AbstractMap.SimpleEntry
 */
public class Entry<T, U> extends AbstractMap.SimpleEntry<T, U> {
    /**
     * Creates a function that maps the value of an entry to another value.
     * 
     * @param <T> The key type.
     * @param <U> The value type.
     * @param <V> The new value type.
     * @param mapper A mapping function.
     * @return A function that maps the value of an entry to another value.
     */
    public static <T, U, V> Function<Entry<T, U>, Entry<T, V>> valueMapper(Function<U, V> mapper) {
        return mapper(Function.identity(), mapper);
    }

    /**
     * Creates a function that maps the key of an entry to another value.
     * 
     * @param <T> The key type.
     * @param <U> The value type.
     * @param <V> The new key type.
     * @param mapper A mapping function.
     * @return A function that maps the key of an entry to another value.
     */
    public static <T, U, V> Function<Entry<T, U>, Entry<V, U>> keyMapper(Function<T, V> mapper) {
        return mapper(mapper, Function.identity());
    }

    /**
     * Creates a function that maps the key and value of an entry to another value.
     * 
     * @param <T> The key type.
     * @param <U> The value type.
     * @param <V> The new key type.
     * @param <W> The new value type.
     * @param keyMapper A mapping function for the key.
     * @param valueMapper A mapping function for the value.
     * @return A function that maps the key and value of an entry to another value.
     */
    public static <T, U, V, W> Function<Entry<T, U>, Entry<V, W>> mapper(Function<T, V> keyMapper, Function<U, W> valueMapper) {
        return entry -> new Entry<>(keyMapper.apply(entry.getKey()), valueMapper.apply(entry.getValue()));
    }

    /**
     * Creates a new entry.
     * 
     * @param key The key.
     * @param value The value.
     */
    public Entry(T key, U value) {
        super(key, value);
    }
}
