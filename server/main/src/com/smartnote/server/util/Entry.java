package com.smartnote.server.util;

import java.util.AbstractMap;
import java.util.function.Function;

public class Entry<T, U> extends AbstractMap.SimpleEntry<T, U> {
    public static <T, U, V> Function<Entry<T, U>, Entry<T, V>> valueMapper(Function<U, V> mapper) {
        return entry -> new Entry<>(entry.getKey(), mapper.apply(entry.getValue()));
    }

    public Entry(T key, U value) {
        super(key, value);
    }
}
